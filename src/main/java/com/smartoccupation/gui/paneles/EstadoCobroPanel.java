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

/**
 * Panel de la Interfaz Gráfica (GUI) para la visualización y seguimiento del
 * Estado de Cobro de los Alquileres. 💰
 * Muestra el total pagado, el saldo pendiente y el estado general de cada alquiler.
 *
 * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public class EstadoCobroPanel extends javax.swing.JPanel {

    // Referencia al servicio de lógica de negocio para Alquileres.
    private final AlquilerService alquilerService;
    // Referencia al servicio de lógica de negocio para Pagos.
    private final PagoService pagoService;

    // Modelo de datos de la tabla (útil para la manipulación programática de filas).
    private DefaultTableModel tablaModel;

    /**
     * Constructor que recibe por inyección de dependencias los servicios necesarios.
     *
     * @param alquilerService Servicio para obtener datos de los alquileres.
     * @param pagoService Servicio para obtener y sumar los pagos asociados a cada alquiler.
     */
    public EstadoCobroPanel(AlquilerService alquilerService, PagoService pagoService) {
        // Validación de nulidad de las dependencias.
        if (alquilerService == null) {
            throw new IllegalArgumentException("AlquilerService no puede ser nulo");
        }
        if (pagoService == null) {
            throw new IllegalArgumentException("PagoService no puede ser nulo");
        }

        this.alquilerService = alquilerService;
        this.pagoService = pagoService;

        initComponents(); // Inicializa los componentes visuales autogenerados.

        postInit(); // Llama a la configuración adicional tras la inicialización de componentes.
    }

    /**
     * Configuración adicional de componentes, eventos y carga inicial de datos.
     */
    private void postInit() {
        // 🔹 Configurar la tabla y las columnas
        configurarTabla();

        // 🔹 Inicializar el ComboBox de estado con la opción "Todos"
        // Sobrescribe el modelo generado para incluir "Todos" como primera opción.
        cbEstado.setModel(new DefaultComboBoxModel<>(new String[]{"Todos", "Pendiente", "Retrasado", "Pagado"}));

        // 🔹 Configurar listeners
        configurarEventos();

        // 🔹 Carga inicial de la tabla
        cargarTablaAlquileres();
    }

    /**
     * Define la estructura de columnas del {@code DefaultTableModel}.
     */
    private void configurarTabla() {
        // 🔹 Se redefine la estructura de la tabla para mostrar más detalles de cobro
        tablaModel = new DefaultTableModel(
                // Nuevas columnas para mostrar Total Pagado y Pendiente
                new Object[]{"ID Expediente", "Cliente", "Vivienda", "Total Estimado", "Total Pagado", "Pendiente", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Las celdas no son editables.
            }
        };
        // 🔹 Aplicamos el nuevo modelo a la tabla autogenerada
        tablaCobros.setModel(tablaModel);
    }

    /**
     * Configura los ActionListeners para los elementos de control.
     */
    private void configurarEventos() {
        // 🔹 Refrescar (Funciona como recargar sin filtro)
        btnRefrescar.addActionListener(e -> cargarTablaAlquileres());

        // 🔹 Implementación del filtro: Recargar al cambiar el ComboBox cbEstado
        cbEstado.addActionListener(e -> cargarTablaAlquileres());

    }

    /**
     * Carga los alquileres, calcula el estado de cobro (Pagado, Pendiente, Retrasado)
     * y filtra por el estado seleccionado en el ComboBox.
     */
    private void cargarTablaAlquileres() {
        tablaModel.setRowCount(0); // Limpia las filas existentes antes de recargar.

        // Obtener el filtro seleccionado (ej: "Retrasado", "Todos")
        String estadoSeleccionado = (String) cbEstado.getSelectedItem();
        if (estadoSeleccionado == null) {
            estadoSeleccionado = "Todos"; // Valor por defecto.
        }

        try {
            // Obtener todos los alquileres
            List<Alquiler> lista = alquilerService.obtenerTodos();
            if (lista == null || lista.isEmpty()) {
                return;
            }

            for (Alquiler a : lista) {

                // 1. CÁLCULO DE TOTALES
                // Asegura que el total estimado no sea null, usando ZERO si lo es.
                BigDecimal total = a.getPrecioTotalEstimado() != null ? a.getPrecioTotalEstimado() : BigDecimal.ZERO;

                // Obtener pagos del servicio para el expediente actual
                List<Pago> pagos = pagoService.obtenerPagosPorExpediente(a.getNumeroExpediente());
                // Sumar la cantidad de todos los pagos
                BigDecimal totalPagado = pagos.stream()
                        .map(p -> p.getCantidad() != null ? p.getCantidad() : BigDecimal.ZERO) // <--- Manejo de null para robustez en Pagos
                        .reduce(BigDecimal.ZERO, BigDecimal::add); // Suma todas las cantidades.

                // Calcular el saldo pendiente
                BigDecimal pendiente = total.subtract(totalPagado);

                // 2. DETERMINACIÓN DEL ESTADO
                String estado;
                // Si el saldo pendiente es mayor que cero (PENDIENTE DE COBRO)
                if (pendiente.compareTo(BigDecimal.ZERO) > 0) {
                    // Si ya se pagó algo, el estado es Retrasado (hay avance, pero no está completo)
                    if (totalPagado.compareTo(BigDecimal.ZERO) > 0) {
                         estado = "Retrasado";
                    } else {
                        // Si no se pagó nada, está Pendiente (no ha iniciado el cobro)
                         estado = "Pendiente";
                    }
                } else {
                    // Si no hay pendiente o está sobrepagado (pendiente <= 0)
                    estado = "Pagado";
                }

                // 3. APLICACIÓN DEL FILTRO
                // Solo se añade la fila si se selecciona "Todos" o si el estado coincide con el filtro.
                if (estadoSeleccionado.equals("Todos") || estadoSeleccionado.equals(estado)) {

                    // Construcción del nombre completo del cliente (manejo de null)
                    String clienteLabel = a.getCliente() != null
                            ? a.getCliente().getNombre() + " " + a.getCliente().getPrimerApellido() + " " + a.getCliente().getSegundoApellido()
                            : String.valueOf(a.getIdCliente());

                    // Construcción de la dirección de la vivienda (manejo de null)
                    String viviendaLabel = a.getVivienda() != null
                            ? a.getVivienda().getDireccion()
                            : String.valueOf(a.getIdVivienda());

                    // Añadir la fila con los nuevos campos
                    tablaModel.addRow(new Object[]{
                        a.getNumeroExpediente(),
                        clienteLabel,
                        viviendaLabel,
                        // Se usa toPlainString para evitar notación científica en BigDecimals
                        total.toPlainString(),
                        totalPagado.toPlainString(),
                        pendiente.toPlainString(),
                        estado
                    });
                }
            }

        } catch (Exception ex) {
            // Manejo de errores generales durante la carga de datos
            FormUtils.mostrarError(this, "Error cargando alquileres: " + ex.getMessage());
        }
    }

    // Métodos Getters públicos para acceder a componentes (útil para pruebas o interacciones externas)

    public JTable getTablaCobros() {
        return tablaCobros;
    }

    public JComboBox<String> getCbEstado() {
        return cbEstado;
    }

    // Solo para testing (o para forzar un refresco desde otra clase)
    public void refrescarTabla() {
        cargarTablaAlquileres();
    }

    public JButton getBtnRefrescar() {
        return btnRefrescar;
    }

    /**
     * Método autogenerado por el diseñador de GUI para inicializar los componentes.
     */
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
