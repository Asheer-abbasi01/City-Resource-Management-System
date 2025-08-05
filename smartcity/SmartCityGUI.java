package smartcity;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SmartCityGUI extends JFrame {
    private User currentUser;
    private CityRepository<CityResource> repository;
    private DefaultTableModel tableModel;
    private JTable resourceTable;
    private CityMapPanel mapPanel;
    private ResourceChartPanel chartPanel;
    private JTextArea reportArea;
    private static JTextArea reportAreaStatic;
    private JLabel metricsLabel;

    private JButton addButton, updateButton, deleteButton, reportButton, saveButton, logoutButton;
    private final String DATA_FILE = "city_resources.dat";
    private boolean isAdmin = false;
    private boolean hasUnsavedChanges = false;
    private final AtomicBoolean running = new AtomicBoolean(true);

    public SmartCityGUI(User user) {
        this.currentUser = user;
        this.isAdmin = currentUser.getRole().equals("ADMIN");
        this.repository = new CityRepository<>();
        CityResource.setRepository(repository);

        setTitle("Smart City Resource Management System"
                + (currentUser.getRole().equals("GUEST") ? "" : " - " + currentUser.getUsername()));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        if (currentUser.getRole().equals("GUEST")) {
            createInitialPanel();
        } else {
            createTopPanel();
            createCenterPanel();
            createBottomPanel();
            configureUserAccess();
            loadFromFileOrInit();
            refreshTable();
        }

        pack();
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        if (!currentUser.getRole().equals("GUEST")) {
            startDynamicUpdates();
        }
    }

    private void loadFromFileOrInit() {
        File dataFile = new File(DATA_FILE);
        if (dataFile.exists()) {
            try {
                repository.loadFromFile(DATA_FILE);
                if (reportArea != null) {
                    reportArea.append("Data loaded successfully from " + DATA_FILE + " at " +
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
                }
            } catch (IOException e) {
                if (reportArea != null) {
                    reportArea.append("IO Error loading " + DATA_FILE + ": " + e.getMessage()
                            + ". Initializing default data at " +
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
                }
                JOptionPane.showMessageDialog(this,
                        "IO Error loading data: " + e.getMessage() + ". Running with default settings.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                initializeSampleData();
            } catch (ClassNotFoundException e) {
                if (reportArea != null) {
                    reportArea.append("ClassNotFoundError loading " + DATA_FILE + ": " + e.getMessage()
                            + ". Initializing default data at " +
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
                }
                JOptionPane.showMessageDialog(this,
                        "ClassNotFoundError loading data: " + e.getMessage() + ". Running with default settings.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                initializeSampleData();
            } catch (Exception e) {
                if (reportArea != null) {
                    reportArea.append("Unexpected error loading " + DATA_FILE + ": " + e.getMessage()
                            + ". Initializing default data at " +
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
                }
                JOptionPane.showMessageDialog(this,
                        "Unexpected error loading data: " + e.getMessage() + ". Running with default settings.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                initializeSampleData();
            }
        } else {
            if (reportArea != null) {
                reportArea.append("No data file found. Initializing default data at " +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
            }
            initializeSampleData();
        }
    }

    private void createInitialPanel() {
        JPanel initialPanel = new JPanel(new GridBagLayout());
        initialPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel titleLabel = new JLabel("Smart City Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        initialPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        JButton publicButton = new JButton("Public Access");
        publicButton.setFont(new Font("Arial", Font.BOLD, 18));
        publicButton.setPreferredSize(new Dimension(200, 50));
        publicButton.addActionListener(_ -> {
            dispose();
            SwingUtilities.invokeLater(() -> {
                SmartCityGUI gui = new SmartCityGUI(new User("public", "PUBLIC"));
                gui.setVisible(true);
            });
        });
        initialPanel.add(publicButton, gbc);

        gbc.gridy = 2;
        JButton adminButton = new JButton("Admin");
        adminButton.setFont(new Font("Arial", Font.BOLD, 18));
        adminButton.setPreferredSize(new Dimension(200, 50));
        adminButton.addActionListener(_ -> showAdminLoginDialog());
        initialPanel.add(adminButton, gbc);

        add(initialPanel, BorderLayout.CENTER);
    }

    private void showAdminLoginDialog() {
        JDialog loginDialog = new JDialog(this, "Admin Login", true);
        loginDialog.setLayout(new GridBagLayout());
        loginDialog.getContentPane().setBackground(new Color(135, 206, 235));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginDialog.add(new JLabel("Admin Login"), gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        loginDialog.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        JTextField usernameField = new JTextField(15);
        loginDialog.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        loginDialog.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(15);
        loginDialog.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(_ -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            if (username.equals("admin") && password.equals("admin123")) {
                dispose();
                SwingUtilities.invokeLater(() -> {
                    SmartCityGUI gui = new SmartCityGUI(new User(username, "ADMIN"));
                    gui.setVisible(true);
                });
                loginDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(loginDialog, "Invalid username or password!", "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        loginDialog.add(loginButton, gbc);

        loginDialog.pack();
        loginDialog.setLocationRelativeTo(this);
        loginDialog.setVisible(true);
    }

    private void createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createTitledBorder("Resource Management"));

        addButton = new JButton("Add Resource");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        reportButton = new JButton("Generate Report");
        saveButton = new JButton("Save Data");
        logoutButton = new JButton("Logout");

        logoutButton = new JButton("Logout");

        // Custom styling for logout button
        logoutButton.setBackground(new Color(220, 53, 69)); // Bootstrap danger red
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        logoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(new Color(200, 35, 51));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(new Color(220, 53, 69)); // Original red
            }
        });
        logoutButton.setPreferredSize(new Dimension(80, 30));
        logoutButton.setMargin(new Insets(5, 10, 5, 10));

        topPanel.add(addButton);
        topPanel.add(updateButton);
        topPanel.add(deleteButton);
        topPanel.add(reportButton);
        topPanel.add(saveButton);
        topPanel.add(logoutButton);

        addButton.addActionListener(this::addResource);
        updateButton.addActionListener(this::updateResource);
        deleteButton.addActionListener(this::deleteResource);
        reportButton.addActionListener(this::generateReport);
        saveButton.addActionListener(this::saveData);
        logoutButton.addActionListener(this::logout);

        add(topPanel, BorderLayout.NORTH);
    }

    private void createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        String[] columns = { "ID", "Type", "Location", "Status", "Last Updated" };
        tableModel = new DefaultTableModel(columns, 0);
        resourceTable = new JTable(tableModel);
        resourceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resourceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedResource();
            }
        });
        JScrollPane tableScrollPane = new JScrollPane(resourceTable);
        tableScrollPane.setPreferredSize(new Dimension(600, 300));
        mapPanel = new CityMapPanel();
        chartPanel = new ResourceChartPanel();
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(mapPanel, BorderLayout.CENTER);
        rightPanel.add(chartPanel, BorderLayout.SOUTH);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScrollPane, rightPanel);
        splitPane.setDividerLocation(600);
        centerPanel.add(splitPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void createBottomPanel() {
        if (reportArea == null) {
            JPanel bottomPanel = new JPanel(new BorderLayout());
            reportArea = new JTextArea(8, 50);
            reportAreaStatic = reportArea;
            reportArea.setEditable(false);
            reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JScrollPane reportScrollPane = new JScrollPane(reportArea);
            reportScrollPane.setBorder(BorderFactory.createTitledBorder("Reports & Alerts"));
            JPanel metricsPanel = new JPanel(new FlowLayout());
            metricsLabel = new JLabel("City Metrics: Loading...");
            metricsPanel.add(metricsLabel);
            metricsPanel.setBorder(BorderFactory.createTitledBorder("Real-time Metrics"));
            bottomPanel.add(reportScrollPane, BorderLayout.CENTER);
            bottomPanel.add(metricsPanel, BorderLayout.SOUTH);
            add(bottomPanel, BorderLayout.SOUTH);
        }
    }

    private void configureUserAccess() {
        addButton.setEnabled(isAdmin);
        updateButton.setEnabled(isAdmin);
        deleteButton.setEnabled(isAdmin);
        saveButton.setEnabled(isAdmin);
        if (!isAdmin && currentUser.getRole().equals("PUBLIC")) {
            if (reportArea != null) {
                reportArea.append("Running in PUBLIC mode - View only access\n");
                reportArea.append("Dynamic simulation started - Real-time updates every 5 seconds\n");
                reportArea.append("Emergency scenarios will be simulated every 30 seconds\n");
            }
        }
    }

    private void addResource(ActionEvent e) {
        if (!isAdmin)
            return;
        JDialog addDialog = new JDialog(this, "Add Resource", true);
        addDialog.setLayout(new GridBagLayout());
        addDialog.getContentPane().setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Select Resource Type");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        addDialog.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        addDialog.add(new JLabel("Resource Type:"), gbc);
        JComboBox<String> resourceTypeCombo = new JComboBox<>(new String[] { "Transport", "Power", "Emergency" });
        gbc.gridx = 1;
        addDialog.add(resourceTypeCombo, gbc);

        JPanel specificPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcSpecific = new GridBagConstraints();
        gbcSpecific.insets = new Insets(5, 5, 5, 5);
        gbcSpecific.fill = GridBagConstraints.HORIZONTAL;

        JTextField idFieldDialog = new JTextField(15);
        JTextField locationFieldDialog = new JTextField(15);
        JTextField statusFieldDialog = new JTextField(15);
        JComboBox<String> vehicleTypeComboDialog = new JComboBox<>(new String[] { "Bus", "Train" });
        JTextField passengerCapacityFieldDialog = new JTextField(10);
        JTextField fuelConsumptionRateFieldDialog = new JTextField(10);
        JTextField energyOutputFieldDialog = new JTextField(10);
        JComboBox<String> powerTypeComboDialog = new JComboBox<>(new String[] { "Solar", "Nuclear" });
        JTextField responseTimeFieldDialog = new JTextField(10);
        JComboBox<String> serviceTypeComboDialog = new JComboBox<>(new String[] { "Police", "Fire" });
        JTextField callsHandledFieldDialog = new JTextField(10);

        resourceTypeCombo.addActionListener(_ -> {
            specificPanel.removeAll();
            gbcSpecific.gridy = 0;
            if ("Transport".equals(resourceTypeCombo.getSelectedItem())) {
                gbcSpecific.gridx = 0;
                specificPanel.add(new JLabel("ID:"), gbcSpecific);
                gbcSpecific.gridx = 1;
                specificPanel.add(idFieldDialog, gbcSpecific);

                gbcSpecific.gridx = 0;
                gbcSpecific.gridy = 1;
                specificPanel.add(new JLabel("Location:"), gbcSpecific);
                gbcSpecific.gridx = 1;
                specificPanel.add(locationFieldDialog, gbcSpecific);

                gbcSpecific.gridx = 0;
                gbcSpecific.gridy = 2;
                specificPanel.add(new JLabel("Status:"), gbcSpecific);
                gbcSpecific.gridx = 1;
                specificPanel.add(statusFieldDialog, gbcSpecific);

                gbcSpecific.gridx = 0;
                gbcSpecific.gridy = 3;
                specificPanel.add(new JLabel("Vehicle Type:"), gbcSpecific);
                gbcSpecific.gridx = 1;
                specificPanel.add(vehicleTypeComboDialog, gbcSpecific);

                gbcSpecific.gridx = 0;
                gbcSpecific.gridy = 4;
                specificPanel.add(new JLabel("Passenger Capacity:"), gbcSpecific);
                gbcSpecific.gridx = 1;
                specificPanel.add(passengerCapacityFieldDialog, gbcSpecific);

                gbcSpecific.gridx = 0;
                gbcSpecific.gridy = 5;
                specificPanel.add(new JLabel("Fuel Consumption Rate:"), gbcSpecific);
                gbcSpecific.gridx = 1;
                specificPanel.add(fuelConsumptionRateFieldDialog, gbcSpecific);
            } else if ("Power".equals(resourceTypeCombo.getSelectedItem())) {
                gbcSpecific.gridx = 0;
                specificPanel.add(new JLabel("ID:"), gbcSpecific);
                gbcSpecific.gridx = 1;
                specificPanel.add(idFieldDialog, gbcSpecific);

                gbcSpecific.gridx = 0;
                gbcSpecific.gridy = 1;
                specificPanel.add(new JLabel("Location:"), gbcSpecific);
                gbcSpecific.gridx = 1;
                specificPanel.add(locationFieldDialog, gbcSpecific);

                gbcSpecific.gridx = 0;
                gbcSpecific.gridy = 2;
                specificPanel.add(new JLabel("Status:"), gbcSpecific);
                gbcSpecific.gridx = 1;
                specificPanel.add(statusFieldDialog, gbcSpecific);

                gbcSpecific.gridx = 0;
                gbcSpecific.gridy = 3;
                specificPanel.add(new JLabel("Energy Output (MW):"), gbcSpecific);
                gbcSpecific.gridx = 1;
                specificPanel.add(energyOutputFieldDialog, gbcSpecific);

                gbcSpecific.gridx = 0;
                gbcSpecific.gridy = 4;
                specificPanel.add(new JLabel("Power Type:"), gbcSpecific);
                gbcSpecific.gridx = 1;
                specificPanel.add(powerTypeComboDialog, gbcSpecific);
            } else if ("Emergency".equals(resourceTypeCombo.getSelectedItem())) {
                gbcSpecific.gridx = 0;
                specificPanel.add(new JLabel("ID:"), gbcSpecific);
                gbcSpecific.gridx = 1;
                specificPanel.add(idFieldDialog, gbcSpecific);

                gbcSpecific.gridx = 0;
                gbcSpecific.gridy = 1;
                specificPanel.add(new JLabel("Location:"), gbcSpecific);
                gbcSpecific.gridx = 1;
                specificPanel.add(locationFieldDialog, gbcSpecific);

                gbcSpecific.gridx = 0;
                gbcSpecific.gridy = 2;
                specificPanel.add(new JLabel("Status:"), gbcSpecific);
                gbcSpecific.gridx = 1;
                specificPanel.add(statusFieldDialog, gbcSpecific);

                gbcSpecific.gridx = 0;
                gbcSpecific.gridy = 3;
                specificPanel.add(new JLabel("Service Type:"), gbcSpecific);
                gbcSpecific.gridx = 1;
                specificPanel.add(serviceTypeComboDialog, gbcSpecific);

                gbcSpecific.gridx = 0;
                gbcSpecific.gridy = 4;
                specificPanel.add(new JLabel("Response Time (min):"), gbcSpecific);
                gbcSpecific.gridx = 1;
                specificPanel.add(responseTimeFieldDialog, gbcSpecific);

                gbcSpecific.gridx = 0;
                gbcSpecific.gridy = 5;
                specificPanel.add(new JLabel("Calls Handled:"), gbcSpecific);
                gbcSpecific.gridx = 1;
                specificPanel.add(callsHandledFieldDialog, gbcSpecific);
            }
            addDialog.pack();
        });

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        addDialog.add(specificPanel, gbc);

        gbc.gridy = 3;
        JButton addResourceButton = new JButton("Add Resource");
        addResourceButton.setFont(new Font("Arial", Font.BOLD, 14));
        addResourceButton.addActionListener(_ -> {
            String id = idFieldDialog.getText().trim();
            String location = locationFieldDialog.getText().trim();
            String status = statusFieldDialog.getText().trim();
            String resourceType = (String) resourceTypeCombo.getSelectedItem();
            if (id.isEmpty() || location.isEmpty() || status.isEmpty()) {
                JOptionPane.showMessageDialog(addDialog, "Please fill all required fields", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                CityResource resource = createResourceByType(id, location, status, resourceType, vehicleTypeComboDialog,
                        passengerCapacityFieldDialog, fuelConsumptionRateFieldDialog, energyOutputFieldDialog,
                        powerTypeComboDialog, serviceTypeComboDialog, responseTimeFieldDialog, callsHandledFieldDialog);
                repository.add(resource);
                hasUnsavedChanges = true;
                refreshTable();
                if (reportArea != null) {
                    reportArea.append("Added new resource: " + resource.toString() + " at " +
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
                }
                addDialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(addDialog, "Error adding resource: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        addDialog.add(addResourceButton, gbc);

        addDialog.setSize(400, 350);
        addDialog.setLocationRelativeTo(this);
        addDialog.setVisible(true);
    }

    private CityResource createResourceByType(String id, String location, String status, String resourceType,
            JComboBox<String> vehicleTypeCombo, JTextField passengerCapacityField, JTextField fuelConsumptionRateField,
            JTextField energyOutputField, JComboBox<String> powerTypeCombo, JComboBox<String> serviceTypeCombo,
            JTextField responseTimeField, JTextField callsHandledField) {
        try {
            if ("Transport".equals(resourceType)) {
                int passengerCapacity = Integer.parseInt(passengerCapacityField.getText().trim());
                double fuelConsumptionRate = Double.parseDouble(fuelConsumptionRateField.getText().trim());
                TransportUnit unit = new TransportUnit(id, location, status,
                        (String) vehicleTypeCombo.getSelectedItem(), passengerCapacity, fuelConsumptionRate);
                unit.setCurrentPassengers(0);
                return unit;
            } else if ("Power".equals(resourceType)) {
                double energyOutput = Double.parseDouble(energyOutputField.getText().trim());
                return new PowerStation(id, location, status, energyOutput, (String) powerTypeCombo.getSelectedItem());
            } else if ("Emergency".equals(resourceType)) {
                int responseTime = Integer.parseInt(responseTimeField.getText().trim());
                int callsHandled = Integer.parseInt(callsHandledField.getText().trim());
                return new EmergencyService(id, location, status, (String) serviceTypeCombo.getSelectedItem(),
                        responseTime, callsHandled);
            }
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid numeric input: " + ex.getMessage());
        }
        throw new IllegalArgumentException("Unknown resource type: " + resourceType);
    }

    private void updateResource(ActionEvent e) {
        if (!isAdmin)
            return;
        int selectedRow = resourceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a resource to update", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        String resourceId = (String) tableModel.getValueAt(selectedRow, 0);
        CityResource resource = repository.get(resourceId);
        if (resource != null) {
            JDialog updateDialog = new JDialog(this, "Update Resource", true);
            updateDialog.setLayout(new GridBagLayout());
            updateDialog.getContentPane().setBackground(Color.WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0;
            gbc.gridy = 0;
            updateDialog.add(new JLabel("ID:"), gbc);
            JTextField idFieldDialog = new JTextField(resource.getResourceID(), 15);
            idFieldDialog.setEditable(false);
            gbc.gridx = 1;
            updateDialog.add(idFieldDialog, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            updateDialog.add(new JLabel("Location:"), gbc);
            JTextField locationFieldDialog = new JTextField(resource.getLocation(), 15);
            gbc.gridx = 1;
            updateDialog.add(locationFieldDialog, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            updateDialog.add(new JLabel("Status:"), gbc);
            JTextField statusFieldDialog = new JTextField(resource.getStatus(), 15);
            gbc.gridx = 1;
            updateDialog.add(statusFieldDialog, gbc);

            JTextField passengerField = null;
            if (resource instanceof TransportUnit) {
                TransportUnit transport = (TransportUnit) resource;
                gbc.gridx = 0;
                gbc.gridy = 3;
                updateDialog.add(new JLabel("Current Passengers:"), gbc);
                passengerField = new JTextField(String.valueOf(transport.getCurrentPassengers()), 15);
                gbc.gridx = 1;
                updateDialog.add(passengerField, gbc);
            }

            JTextField finalPassengerField = passengerField;

            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.gridwidth = 2;
            JButton saveChangesButton = new JButton("Save Changes");
            saveChangesButton.addActionListener(_ -> {
                resource.location = locationFieldDialog.getText().trim();
                resource.setStatus(statusFieldDialog.getText().trim());
                if (resource instanceof TransportUnit && finalPassengerField != null) {
                    TransportUnit transport = (TransportUnit) resource;
                    try {
                        int passengers = Integer.parseInt(finalPassengerField.getText().trim());
                        transport.setCurrentPassengers(passengers);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(updateDialog, "Invalid passenger count", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                hasUnsavedChanges = true;
                refreshTable();
                if (reportArea != null) {
                    reportArea.append("Updated resource " + resourceId + " status to: " + resource.getStatus() +
                            " at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
                }
                updateDialog.dispose();
            });
            updateDialog.add(saveChangesButton, gbc);

            updateDialog.pack();
            updateDialog.setLocationRelativeTo(this);
            updateDialog.setVisible(true);
        }
    }

    private void deleteResource(ActionEvent e) {
        if (!isAdmin)
            return;
        int selectedRow = resourceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a resource to delete", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        String resourceId = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete resource: " + resourceId + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            repository.remove(resourceId);
            hasUnsavedChanges = true;
            refreshTable();
            if (reportArea != null) {
                reportArea.append("Deleted resource: " + resourceId + " at " +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
            }
        }
    }

    private void generateReport(ActionEvent e) {
        int selectedRow = resourceTable.getSelectedRow();
        if (selectedRow == -1) {
            generateCityReport();
        } else {
            String resourceId = (String) tableModel.getValueAt(selectedRow, 0);
            CityResource resource = repository.get(resourceId);
            if (resource != null) {
                JDialog reportDialog = new JDialog(this, "Resource Report - "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), true);
                reportDialog.setLayout(new BorderLayout());
                JTextArea reportTextArea = new JTextArea(resource.generateUsageReport());
                reportTextArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(reportTextArea);
                reportDialog.add(scrollPane, BorderLayout.CENTER);
                JButton okButton = new JButton("OK");
                okButton.addActionListener(_ -> reportDialog.dispose());
                reportDialog.add(okButton, BorderLayout.SOUTH);
                reportDialog.pack();
                reportDialog.setLocationRelativeTo(this);
                reportDialog.setVisible(true);
            }
        }
    }

    private void generateCityReport() {
        StringBuilder report = new StringBuilder();
        report.append("Resource Report - ")
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        ArrayList<CityResource> resources = repository.getAll();
        report.append("Transport Units: ").append(resources.stream().filter(r -> r instanceof TransportUnit).count())
                .append("\n");
        report.append("Power Stations: ").append(resources.stream().filter(r -> r instanceof PowerStation).count())
                .append("\n");
        for (CityResource resource : resources) {
            if (resource instanceof PowerStation) {
                PowerStation ps = (PowerStation) resource;
                report.append("Power Station ").append(ps.getResourceID()).append(": ").append(ps.getEnergyOutput())
                        .append("MW output, Type: ").append(ps.getPowerType()).append("\n");
            }
        }
        report.append("Emergency Services: ")
                .append(resources.stream().filter(r -> r instanceof EmergencyService).count()).append("\n\n");
        report.append("Maintenance Costs:\n");
        for (CityResource resource : resources) {
            report.append("- ").append(resource.getResourceID()).append(": $")
                    .append(String.format("%.2f", resource.calculateMaintenanceCost())).append("\n");
        }
        report.append("Total Maintenance Cost: $").append(String.format("%.2f", CityResource.getTotalMaintenanceCost()))
                .append("\n");
        report.append("Total Passengers: ").append(CityResource.getTotalPassengers()).append("\n");
        report.append("Total Energy Usage: ").append(String.format("%.2f", CityResource.getTotalEnergyUsage()))
                .append(" MW\n");

        JDialog reportDialog = new JDialog(this,
                "Resource Report - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                true);
        reportDialog.setLayout(new BorderLayout());
        JTextArea reportTextArea = new JTextArea(report.toString());
        reportTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reportTextArea);
        reportDialog.add(scrollPane, BorderLayout.CENTER);
        JButton okButton = new JButton("OK");
        okButton.addActionListener(_ -> reportDialog.dispose());
        reportDialog.add(okButton, BorderLayout.SOUTH);
        reportDialog.pack();
        reportDialog.setLocationRelativeTo(this);
        reportDialog.setVisible(true);
    }

    private void saveData(ActionEvent e) {
        if (!isAdmin)
            return;
        try {
            repository.saveToFile(DATA_FILE);
            hasUnsavedChanges = false;
            JOptionPane.showMessageDialog(this, "Data saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            if (reportArea != null) {
                reportArea.append("Data saved to " + DATA_FILE + " at "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            if (reportArea != null) {
                reportArea.append("Error saving data to " + DATA_FILE + ": " + ex.getMessage() + " at " +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
            }
        }
    }

    private void loadSelectedResource() {
    }

    private void refreshTable() {
        if (tableModel == null) {
            return;
        }
        tableModel.setRowCount(0);
        ArrayList<CityResource> allResources = repository.getAll();
        for (CityResource resource : allResources) {
            Object[] row = {
                    resource.getResourceID(),
                    resource.getClass().getSimpleName(),
                    resource.getLocation(),
                    resource.getStatus(),
                    resource.lastUpdated.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            };
            tableModel.addRow(row);
        }
        if (mapPanel != null)
            mapPanel.updateResources(allResources);
        if (chartPanel != null)
            chartPanel.updateResources(allResources);
        updateMetrics();
    }

    private void updateMetrics() {
        if (metricsLabel != null) {
            String metrics = String.format(
                    "Resources: %d | Passengers: %d | Energy: %.2f MW | Maintenance: $%.2f | Emergencies: %d",
                    CityResource.getTotalResources(),
                    CityResource.getTotalPassengers(),
                    CityResource.getTotalEnergyUsage(),
                    CityResource.getTotalMaintenanceCost(),
                    EmergencyService.getTotalEmergencyResponses());
            metricsLabel.setText(metrics);
        }
    }

    private void initializeSampleData() {
        TransportUnit bus = new TransportUnit("BUS001", "Downtown Hub", "Active", "Bus", 50, 15.5);
        bus.setCurrentPassengers(30);
        repository.add(bus);
        TransportUnit train = new TransportUnit("TRAIN001", "Central Station", "Active", "Train", 200, 25.0);
        train.setCurrentPassengers(150);
        repository.add(train);
        PowerStation solar = new PowerStation("SOLAR001", "Industrial Zone", "Operational", 500.0, "Solar");
        solar.addConsumer(new Consumer("C001", "Residential", 100.0));
        repository.add(solar);
        PowerStation nuclear = new PowerStation("NUCLEAR001", "Power District", "Operational", 1000.0, "Nuclear");
        nuclear.addConsumer(new Consumer("C002", "Commercial", 200.0));
        repository.add(nuclear);
        repository.add(new EmergencyService("FIRE001", "Fire Station Alpha", "Available", "Fire", 4, 10));
        repository.add(new EmergencyService("POLICE001", "Police Precinct 1", "Available", "Police", 6, 15));
    }

    private void startDynamicUpdates() {
        new Thread(() -> {
            while (running.get()) {
                simulateRealTimeUpdates();
                SwingUtilities.invokeLater(this::refreshTable);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();

        new Thread(() -> {
            while (running.get()) {
                for (CityResource resource : repository.getAll()) {
                    if (resource instanceof TransportUnit) {
                        TransportUnit unit = (TransportUnit) resource;
                        int change = new Random().nextInt(11) - 5;
                        unit.adjustRouteBasedOnTraffic(change);
                    }
                }
                SwingUtilities.invokeLater(this::refreshTable);
                try {
                    Thread.sleep(7000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();

        new Thread(() -> {
            while (running.get()) {
                simulateEmergencyScenario();
                SwingUtilities.invokeLater(this::refreshTable);
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    private void simulateRealTimeUpdates() {
        Random rand = new Random();
        for (CityResource resource : repository.getAll()) {
            if (resource instanceof PowerStation) {
                PowerStation power = (PowerStation) resource;
                if (rand.nextDouble() < 0.05) {
                    if (power.getStatus().equals("Operational")) {
                        power.setStatus("Maintenance");
                        if (reportArea != null) {
                            reportArea.append("POWER ALERT: " + power.getResourceID() + " requires maintenance at " +
                                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
                        }
                    }
                }
            }
            if (resource instanceof EmergencyService) {
                EmergencyService emergency = (EmergencyService) resource;
                if (rand.nextDouble() < 0.08 && emergency.getStatus().equals("Available")) {
                    emergency.sendEmergencyAlert("Routine patrol dispatch");
                }
            }
        }
    }

    private void simulateEmergencyScenario() {
        Random rand = new Random();
        ArrayList<CityResource> allResources = repository.getAll();
        if (!allResources.isEmpty()) {
            CityResource randomResource = allResources.get(rand.nextInt(allResources.size()));
            if (randomResource instanceof PowerStation && rand.nextDouble() < 0.3) {
                PowerStation power = (PowerStation) randomResource;
                power.simulateOutage();
                if (reportArea != null) {
                    reportArea.append("EMERGENCY SCENARIO: Power outage at " + power.getLocation() +
                            " at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
                }
            } else if (randomResource instanceof TransportUnit && rand.nextDouble() < 0.2) {
                TransportUnit transport = (TransportUnit) randomResource;
                transport.setStatus("Emergency");
                if (reportArea != null) {
                    reportArea.append("TRANSPORT EMERGENCY: " + transport.getResourceID() + " at "
                            + transport.getLocation() +
                            " at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
                }
                for (CityResource resource : allResources) {
                    if (resource instanceof EmergencyService) {
                        EmergencyService emergency = (EmergencyService) resource;
                        if (emergency.getStatus().equals("Available")) {
                            emergency.sendEmergencyAlert("Transport emergency response needed");
                            break;
                        }
                    }
                }
            }
        }
    }

    private void logout(ActionEvent e) {
        if (isAdmin && hasUnsavedChanges) {
            int result = JOptionPane.showConfirmDialog(this, "You have unsaved changes. Save before logout?",
                    "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION);
            if (result == JOptionPane.CANCEL_OPTION)
                return;
            if (result == JOptionPane.YES_OPTION) {
                saveData(null);
            }
        }
        running.set(false);
        dispose();
        SwingUtilities.invokeLater(() -> new SmartCityGUI(new User("guest", "GUEST")).setVisible(true));
    }

    public static JTextArea getReportArea() {
        return reportAreaStatic;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new SmartCityGUI(new User("guest", "GUEST")).setVisible(true));
    }
}