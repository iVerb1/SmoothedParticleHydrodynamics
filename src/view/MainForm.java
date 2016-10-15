package view;

import org.lwjgl.opengl.Display;
import particle.PhysicsSystem;
import particle.PhysicsSystem2D;
import particle.PhysicsSystem3D;
import particle.solve.EulerSolver;
import particle.solve.MidPointSolver;
import particle.solve.RungeKutta4Solver;
import particle.solve.Solver;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.PopupMenuEvent;
import java.awt.*;
import java.awt.event.*;


public class MainForm  {

    private PhysicsSystem currentPhysicsSystem;
    private PhysicsSystem2D particleSystem2D;
    private PhysicsSystem3D particleSystem3D;

    public static int VIEWPORT_WIDTH = 1280;
    public static int VIEWPORT_HEIGHT = 720;
    private Thread currentThread;


    public MainForm() {
        canvas.setSize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);

        try {
            Display.setParent(canvas);
        }
        catch (Exception e) {
            throw new IllegalStateException();
        }

        setupComponents();

        particleSystem2D = new PhysicsSystem2D();
        particleSystem3D = new PhysicsSystem3D();

        initParticleSystem();
        startDemo();
    }

    private void setupComponents() {
        panel = new JPanel();
        pauseButton = new JButton();
        integrationSchemeLabel = new JLabel();
        simulationLabel = new JLabel();
        resetButton = new JButton();
        simulationComboBox = new JComboBox();
        integrationSchemeComboBox = new JComboBox();
        timeStepLabel = new JLabel();
        timeStepSpinner = new JSpinner(new SpinnerNumberModel(0.02, 0.001, 1, 0.001));
        variableTimeStepLabel = new JLabel();
        variableTimeStepTimeCheckBox = new JCheckBox();
        jSeparator1 = new JSeparator();

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        pauseButton.setText("Pause");
        pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                pauseButtonActionPerformed(evt);
                canvas.requestFocus();
            }
        });

        integrationSchemeLabel.setText("Integration method");

        simulationLabel.setText("Simulation");

        resetButton.setText("Reset");
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                resetButtonActionPerformed(evt);
                canvas.requestFocus();
            }
        });

        simulationComboBox.setModel(new DefaultComboBoxModel(new String[]{"2D - Fluid", "2D - Fluid & Cloth", "2D - Simple", "2D - Cloth", "2D - Cloth & Wall", "2D - Wall", "2D - Angular springs", "3D - Simple", "3D - Cloth", "3D - Cloth & Wall"}));
        simulationComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent evt) {
                simulationComboBoxItemStateChanged(evt);
            }
        });
        simulationComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                canvas.requestFocus();
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });

        integrationSchemeComboBox.setModel(new DefaultComboBoxModel(new String[]{"Euler", "Midpoint", "Runge-Kutta 4"}));
        integrationSchemeComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent evt) {
                integrationSchemeComboBoxItemStateChanged(evt);
            }
        });
        integrationSchemeComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                canvas.requestFocus();
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });

        timeStepLabel.setText("Time step");

        timeStepSpinner.setEnabled(false);
        timeStepSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                timeStepSpinnerStateChanged(evt);
                canvas.requestFocus();
            }
        });

        variableTimeStepLabel.setText("Variable time step");
        variableTimeStepTimeCheckBox.setSelected(true);
        variableTimeStepTimeCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                variableTimeStepTimeCheckStateChanged(evt);
                canvas.requestFocus();
            }
        });

        GroupLayout panelLayout = new GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
                panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(jSeparator1, GroupLayout.Alignment.LEADING)
                                        .addGroup(panelLayout.createSequentialGroup()
                                                .addComponent(simulationLabel)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(simulationComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(GroupLayout.Alignment.LEADING, panelLayout.createSequentialGroup()
                                                .addComponent(timeStepLabel)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(timeStepSpinner, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(panelLayout.createSequentialGroup()
                                                .addComponent(pauseButton, GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(resetButton, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(panelLayout.createSequentialGroup()
                                                .addComponent(integrationSchemeLabel)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(integrationSchemeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(panelLayout.createSequentialGroup()
                                                .addComponent(variableTimeStepLabel)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(variableTimeStepTimeCheckBox)))
                                .addContainerGap())
        );
        panelLayout.setVerticalGroup(
                panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(pauseButton)
                                        .addComponent(resetButton))
                                .addGap(18, 18, 18)
                                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(simulationLabel, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(simulationComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(integrationSchemeLabel)
                                        .addComponent(integrationSchemeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(9, 9, 9)
                                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(variableTimeStepLabel)
                                        .addComponent(variableTimeStepTimeCheckBox))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(timeStepLabel)
                                        .addComponent(timeStepSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(77, Short.MAX_VALUE))
        );

        frame.setTitle("Particle System");
        frame.add(panel, BorderLayout.EAST);
        frame.add(canvas, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    private void integrationSchemeComboBoxItemStateChanged(ItemEvent evt) {
        int i = integrationSchemeComboBox.getSelectedIndex();

        switch (i) {
            case 0:
                currentPhysicsSystem.solver = new EulerSolver();
                break;
            case 1:
                currentPhysicsSystem.solver = new MidPointSolver();
                break;
            case 2:
                currentPhysicsSystem.solver = new RungeKutta4Solver();
                break;
        }
    }

    private void simulationComboBoxItemStateChanged(ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {

            try {
                currentPhysicsSystem.interrupt();
                currentThread.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            particleSystem2D.resetSystem();
            particleSystem3D.resetSystem();

            initParticleSystem();

            startDemo();
        }
    }

    private void variableTimeStepTimeCheckStateChanged(ActionEvent evt) {
        currentPhysicsSystem.toggleVariableTimeStep();
        timeStepSpinner.setEnabled(!timeStepSpinner.isEnabled());
    }

    private void pauseButtonActionPerformed(ActionEvent evt) {
        currentPhysicsSystem.togglePause();
        if (currentPhysicsSystem.isPaused()) {
            pauseButton.setText("Resume");
        }
        else {
            pauseButton.setText("Pause");
        }
    }

    private void timeStepSpinnerStateChanged(ChangeEvent evt) {
        currentPhysicsSystem.timeStep = ((Double) timeStepSpinner.getValue());
    }

    private void resetButtonActionPerformed(ActionEvent evt) {
        currentPhysicsSystem.restart();

        if (currentPhysicsSystem.hasCrashed()) {
            currentThread = new Thread(currentPhysicsSystem);
            currentThread.start();
        }
    }

    private void initParticleSystem() {
        String item = simulationComboBox.getSelectedItem().toString();

        if (item.startsWith("2D")) {
            currentPhysicsSystem = particleSystem2D;
            PhysicsSystem.numDimensions = 2;
        }
        else if (item.startsWith("3D")) {
            currentPhysicsSystem = particleSystem3D;
            PhysicsSystem.numDimensions = 3;
        }
        else
            throw new RuntimeException("Invalid combo box item name!");


        if (item.equals("2D - Fluid")) {
            particleSystem2D.initFluidDemo();
        } else if (item.equals("2D - Fluid & Cloth")) {
            particleSystem2D.initFluidClothDemo();
        }  else if (item.equals("2D - Simple")) {
            particleSystem2D.initSimpleDemo();
        } else if (item.equals("2D - Cloth")) {
            particleSystem2D.initClothDemo();
        } else if (item.equals("2D - Cloth & Wall")) {
            particleSystem2D.initClothWallDemo();
        } else if (item.equals("2D - Wall")) {
            particleSystem2D.initWallDemo();
        } else if (item.equals("2D - Angular springs")) {
            particleSystem2D.initAngularSpringsDemo();
        } else if (item.equals("3D - Simple")) {
            particleSystem3D.initSimpleDemo();
        } else if (item.equals("3D - Cloth")) {
            particleSystem3D.initClothDemo();
        } else if (item.equals("3D - Cloth & Wall")) {
            particleSystem3D.initClothWallDemo();
        } else {
            throw new RuntimeException("Invalid combo box item name!");
        }
    }

    private void startDemo() {
        Solver s = currentPhysicsSystem.solver;
        if (s instanceof EulerSolver)
            integrationSchemeComboBox.setSelectedIndex(0);
        else if (s instanceof MidPointSolver)
            integrationSchemeComboBox.setSelectedIndex(1);
        else if (s instanceof RungeKutta4Solver)
            integrationSchemeComboBox.setSelectedIndex(2);

        variableTimeStepTimeCheckBox.setSelected(currentPhysicsSystem.variableTimeStep);
        timeStepSpinner.setValue(currentPhysicsSystem.timeStep);
        timeStepSpinner.setEnabled(!variableTimeStepTimeCheckBox.isSelected());

        currentThread = new Thread(currentPhysicsSystem);
        currentThread.start();
    }

    private JFrame frame = new JFrame();
    private Canvas canvas = new Canvas();
    private JComboBox integrationSchemeComboBox;
    private JLabel integrationSchemeLabel;
    private JSeparator jSeparator1;
    private JPanel panel;
    private JButton pauseButton;
    private JButton resetButton;
    private JComboBox simulationComboBox;
    private JLabel simulationLabel;
    private JLabel timeStepLabel;
    private JSpinner timeStepSpinner;
    private JLabel variableTimeStepLabel;
    private JCheckBox variableTimeStepTimeCheckBox;

    public static void main(String args[])
    {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm();
            }
        });
    }
}