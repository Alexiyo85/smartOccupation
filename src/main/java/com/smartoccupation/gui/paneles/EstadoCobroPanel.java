package com.smartoccupation.gui.paneles;

import com.smartoccupation.modelo.Alquiler;
import com.smartoccupation.modelo.Pago;
import com.smartoccupation.servicios.AlquilerService;
import com.smartoccupation.servicios.PagoService;
import com.smartoccupation.gui.util.FormUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors; // Aunque no se usa directamente, es buena práctica mantenerla

public class EstadoCobroPanel extends javax.swing.JPanel {

    private final AlquilerService alquilerService;
    private final PagoService pagoService;

    private DefaultTableModel tablaModel;

    public EstadoCobroPanel(AlquilerService alquilerService, PagoService pagoService) {
        if (alquilerService == null) {
            throw new IllegalArgumentException("AlquilerService no puede ser nulo");
        }
        if (pagoService == null) {
            throw new IllegalArgumentException("PagoService no puede ser nulo");
        }

        this.alquilerService = alquilerService;
        this.pagoService = pagoService;

        initComponents();

        postInit();
    }

    private void postInit() {
        // 🔹 Configurar la tabla y las columnas
        configurarTabla();

        // 🔹 Inicializar el ComboBox de estado con la opción "Todos"
        cbEstado.setModel(new DefaultComboBoxModel<>(new String[]{"Todos", "Pendiente", "Retrasado", "Pagado"}));

        // 🔹 Configurar listeners
        configurarEventos();

        // 🔹 Carga inicial de la tabla
        cargarTablaAlquileres();
    }

    private void configurarTabla() {
        // 🔹 Se redefine la estructura de la tabla para mostrar más detalles de cobro
        tablaModel = new DefaultTableModel(
                // Nuevas columnas para mostrar Total Pagado y Pendiente
                new Object[]{"ID Expediente", "Cliente", "Vivienda", "Total Estimado", "Total Pagado", "Pendiente", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        // 🔹 Aplicamos el nuevo modelo a la tabla autogenerada
        tablaCobros.setModel(tablaModel);
    }

    private void configurarEventos() {
        // 🔹 Refrescar (Funciona como recargar sin filtro)
        btnRefrescar.addActionListener(e -> cargarTablaAlquileres());

        // 🔹 Implementación del filtro: Recargar al cambiar el ComboBox cbEstado
        cbEstado.addActionListener(e -> cargarTablaAlquileres());

    }

    /**
     * Carga los alquileres y filtra por el estado de cobro seleccionado. Esto
     * corrige el problema de los filtros que no funcionaban.
     */
    private void cargarTablaAlquileres() {
        tablaModel.setRowCount(0);

        // Obtener el filtro seleccionado (ej: "Retrasado", "Todos")
        String estadoSeleccionado = (String) cbEstado.getSelectedItem();
        if (estadoSeleccionado == null) {
            estadoSeleccionado = "Todos";
        }

        try {
            List<Alquiler> lista = alquilerService.obtenerTodos();
            if (lista == null || lista.isEmpty()) {
                return;
            }

            for (Alquiler a : lista) {

                // 1. CÁLCULO DE TOTALES
                // Asegura que total no sea null
                BigDecimal total = a.getPrecio_total_estimado() != null ? a.getPrecio_total_estimado() : BigDecimal.ZERO;

                // Obtener pagos y sumar, manejando si getCantidad() es null en algún pago
                List<Pago> pagos = pagoService.obtenerPagosPorExpediente(a.getNumero_expediente());
                BigDecimal totalPagado = pagos.stream()
                        .map(p -> p.getCantidad() != null ? p.getCantidad() : BigDecimal.ZERO) // <--- Manejo de null para robustez
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal pendiente = total.subtract(totalPagado);

                // 2. DETERMINACIÓN DEL ESTADO
                String estado;
                if (pendiente.compareTo(BigDecimal.ZERO) > 0) {
                    // Hay un saldo pendiente
                    // Si ya se pagó algo, está Retrasado, sino Pendiente
                    estado = (totalPagado.compareTo(BigDecimal.ZERO) > 0) ? "Retrasado" : "Pendiente";
                } else {
                    // No hay pendiente o está sobrepagado (<= 0)
                    estado = "Pagado";
                }

                // 3. APLICACIÓN DEL FILTRO
                if (estadoSeleccionado.equals("Todos") || estadoSeleccionado.equals(estado)) {

                    // Construcción del nombre completo del cliente
                    String clienteLabel = a.getCliente() != null
                            ? a.getCliente().getNombre() + " " + a.getCliente().getPrimer_apellido() + " " + a.getCliente().getSegundo_apellido()
                            : String.valueOf(a.getId_cliente());

                    // Construcción de la dirección de la vivienda
                    String viviendaLabel = a.getVivienda() != null
                            ? a.getVivienda().getDireccion()
                            : String.valueOf(a.getId_vivienda());

                    // Añadir la fila con los nuevos campos
                    tablaModel.addRow(new Object[]{
                        a.getNumero_expediente(),
                        clienteLabel,
                        viviendaLabel,
                        total.toPlainString(),
                        totalPagado.toPlainString(),
                        pendiente.toPlainString(),
                        estado
                    });
                }
            }

        } catch (Exception ex) {
            FormUtils.mostrarError(this, "Error cargando alquileres: " + ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PanelBusqueda = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cbEstado = new javax.swing.JComboBox<>();
        btnRefrescar = new javax.swing.JButton();
        panelFiltros = new javax.swing.JScrollPane();
        tablaCobros = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Estado del Cobro:");
        PanelBusqueda.add(jLabel1);

        cbEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pendiente", "Pagado", "Retrasado" }));
        PanelBusqueda.add(cbEstado);

        btnRefrescar.setText("Refrescar");
        PanelBusqueda.add(btnRefrescar);

        add(PanelBusqueda, java.awt.BorderLayout.PAGE_START);

        tablaCobros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Cliente", "Fecha", "Monto", "Estado"
            }
        ));
        panelFiltros.setViewportView(tablaCobros);

        add(panelFiltros, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelBusqueda;
    private javax.swing.JButton btnRefrescar;
    private javax.swing.JComboBox<String> cbEstado;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane panelFiltros;
    private javax.swing.JTable tablaCobros;
    // End of variables declaration//GEN-END:variables
}
