package ProyectoReto.eventos.controller;

import ProyectoReto.eventos.model.Factura;
import ProyectoReto.eventos.model.DetalleFactura;
import ProyectoReto.eventos.model.Producto;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import ProyectoReto.eventos.repository.FacturaRepository;
import ProyectoReto.eventos.repository.DetalleFacturaRepository;
import ProyectoReto.eventos.repository.ProductoRepository;
import ProyectoReto.eventos.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;

import jakarta.servlet.http.HttpServletResponse;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.util.stream.Stream;


@Controller
@RequestMapping("/ventas/facturas")
public class FacturaController {

    @Autowired
    private FacturaRepository facturaRepo;

    @Autowired
    private DetalleFacturaRepository detalleRepo;

    @Autowired
    private ProductoRepository productoRepo;

    @Autowired
    private ClienteRepository clienteRepo;


    @GetMapping("/nueva")
    public String nuevaFacturaForm(Model model) {
        model.addAttribute("clientes", clienteRepo.findAll());
        model.addAttribute("productos", productoRepo.findAll());
        return "ventas/factura_form";
    }

    @PostMapping("/guardar")
    public String guardarFactura(
            @RequestParam Integer clienteId,
            @RequestParam(name = "productoId") List<Integer> productoIds,
            @RequestParam(name = "cantidad") List<Integer> cantidades,
            Model model
    ) {
        List<String> errores = new ArrayList<>();
        List<Producto> productos = productoRepo.findAll();
        model.addAttribute("clientes", clienteRepo.findAll());
        model.addAttribute("productos", productos);

        // Validar stock
        for (int i = 0; i < productoIds.size(); i++) {
            Integer prodId = productoIds.get(i);
            Integer cant = cantidades.get(i);
            Producto producto = productoRepo.findById(prodId).orElse(null);

            if (producto == null) {
                errores.add("Producto no válido en la posición " + (i + 1));
            } else if (cant > producto.getStock()) {
                errores.add("❌ El producto '" + producto.getNombre() + "' tiene stock insuficiente. Disponible: " + producto.getStock());
            }
        }

        if (!errores.isEmpty()) {
            model.addAttribute("error", String.join("<br>", errores));
            return "ventas/factura_form";
        }

        // Si todo está bien, continuar generando factura
        Factura factura = new Factura();
        factura.setFecha(LocalDateTime.now());
        factura.setCliente(clienteRepo.findById(clienteId).orElse(null));

        List<DetalleFactura> detalles = new ArrayList<>();
        double total = 0;

        for (int i = 0; i < productoIds.size(); i++) {
            Producto producto = productoRepo.findById(productoIds.get(i)).orElse(null);
            Integer cantidad = cantidades.get(i);

            DetalleFactura detalle = new DetalleFactura();
            detalle.setFactura(factura);
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setSubtotal(producto.getPrecio() * cantidad);

            // Disminuir el stock
            producto.setStock(producto.getStock() - cantidad);
            productoRepo.save(producto);

            total += detalle.getSubtotal();
            detalles.add(detalle);
        }

        factura.setTotal(total);
        factura.setDetalles(detalles);
        facturaRepo.save(factura);

        return "redirect:/ventas/facturas";
    }



    @GetMapping("/pdf/{id}")
    public void generarPdf(@PathVariable Integer id, HttpServletResponse response) throws Exception {
        Factura factura = facturaRepo.findById(id).orElseThrow();
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=factura_" + factura.getId() + ".pdf");

        Document documento = new Document();
        PdfWriter.getInstance(documento, response.getOutputStream());
        documento.open();

        // Título
        Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.BLUE);
        Paragraph titulo = new Paragraph("Ventas PCMAC", fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        documento.add(titulo);

        documento.add(new Paragraph(" "));
        documento.add(new Paragraph("Factura #" + factura.getId(), new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)));
        documento.add(new Paragraph("Fecha: " + factura.getFecha()));
        documento.add(new Paragraph("Cliente: " + factura.getCliente().getNombre() + " " + factura.getCliente().getApellido()));
        documento.add(new Paragraph(" ")); // Espacio

        // Tabla de productos
        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{3, 2, 2, 2});

        // Cabecera con color
        Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        BaseColor azul = new BaseColor(0, 51, 102);
        Stream.of("Producto", "Cantidad", "Precio", "Subtotal").forEach(col -> {
            PdfPCell header = new PdfPCell(new Phrase(col, headFont));
            header.setBackgroundColor(azul);
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setPadding(8);
            tabla.addCell(header);
        });

        // Filas de datos
        Font cuerpoFont = new Font(Font.FontFamily.HELVETICA, 11);
        for (DetalleFactura d : factura.getDetalles()) {
            tabla.addCell(new PdfPCell(new Phrase(d.getProducto().getNombre(), cuerpoFont)));
            tabla.addCell(new PdfPCell(new Phrase(d.getCantidad().toString(), cuerpoFont)));
            tabla.addCell(new PdfPCell(new Phrase("$" + d.getProducto().getPrecio(), cuerpoFont)));
            tabla.addCell(new PdfPCell(new Phrase("$" + d.getSubtotal(), cuerpoFont)));
        }

        documento.add(tabla);

        documento.add(new Paragraph(" "));
        Paragraph total = new Paragraph("Total: $" + factura.getTotal(), new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD));
        total.setAlignment(Element.ALIGN_RIGHT);
        documento.add(total);

        documento.add(new Paragraph(" "));
        Paragraph gracias = new Paragraph("Gracias por su compra.",
                new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC));
        gracias.setAlignment(Element.ALIGN_CENTER);
        documento.add(gracias);

        documento.close();
    }

    @GetMapping
    public String listarFacturas(
            @RequestParam(name = "busqueda", required = false) String busqueda,
            @RequestParam(name = "desde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(name = "hasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Model model
    ) {
        List<Factura> facturas = facturaRepo.findAll();

        // Filtrado por texto (nombre/apellido/id)
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            String filtro = busqueda.trim().toLowerCase();
            facturas = facturas.stream()
                    .filter(f -> {
                        String clienteNombre = f.getCliente().getNombre().toLowerCase();
                        String clienteApellido = f.getCliente().getApellido().toLowerCase();
                        String idStr = String.valueOf(f.getId());
                        return clienteNombre.contains(filtro) || clienteApellido.contains(filtro) || idStr.contains(filtro);
                    })
                    .collect(Collectors.toList());
        }

        // Filtrado por rango de fechas
        if (desde != null && hasta != null) {
            LocalDateTime desdeFecha = desde.atStartOfDay();
            LocalDateTime hastaFecha = hasta.atTime(23, 59, 59);
            facturas = facturas.stream()
                    .filter(f -> !f.getFecha().isBefore(desdeFecha) && !f.getFecha().isAfter(hastaFecha))
                    .collect(Collectors.toList());
        }

        model.addAttribute("facturas", facturas);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);

        return "ventas/facturas";
    }





}
