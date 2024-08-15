package game.subgui;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class CreateLevelOptions extends JDialog {

    public CreateLevelOptions(ItemsFrame parent) {
        super(parent, "Options:", true);

        JPanel baseOptPane = new JPanel(new BorderLayout()) {
            {

                JPanel spinersPane = new JPanel(new GridLayout(0, 1, 3, 3)) {
                    {
                        setBorder(new EmptyBorder(3, 6, 6, 6));

                        JPanel lifePane = new JPanel(new BorderLayout()) {
                            {
                                JLabel lifeLabel = new JLabel("Жизней:");

                                JSpinner lifeSpin = new JSpinner() {
                                    {
                                        setValue(parent.getLives());

                                        addChangeListener(_ -> parent.setLives(Integer.parseInt(getValue().toString())));
                                    }
                                };

                                add(lifeLabel, BorderLayout.WEST);
                                add(lifeSpin, BorderLayout.EAST);
                            }
                        };

                        JPanel deathPane = new JPanel(new BorderLayout()) {
                            {
                                JLabel deathLabel = new JLabel("Попыток:");

                                JSpinner deathSpin = new JSpinner() {
                                    {
                                        setValue(parent.getDeaths());

                                        addChangeListener(_ -> parent.setDeaths(Integer.parseInt(getValue().toString())));
                                    }
                                };

                                add(deathLabel, BorderLayout.WEST);
                                add(deathSpin, BorderLayout.EAST);
                            }
                        };

                        JPanel zombiesPane = new JPanel(new BorderLayout()) {
                            {
                                JLabel zombiesLabel = new JLabel("Zombies:");

                                JSpinner zombiesSpin = new JSpinner() {
                                    {
                                        setValue(parent.getZombiesCount());

                                        addChangeListener(_ -> parent.setZombiesCount(Integer.parseInt(getValue().toString())));
                                    }
                                };

                                add(zombiesLabel, BorderLayout.WEST);
                                add(zombiesSpin, BorderLayout.EAST);
                            }
                        };

                        JPanel skeletonsPane = new JPanel(new BorderLayout()) {
                            {
                                JLabel skeletonsLabel = new JLabel("Skeletons");

                                JSpinner skeletonsSpin = new JSpinner() {
                                    {
                                        setValue(parent.getSkeletonsCount());

                                        addChangeListener(_ -> parent.setSkeletonsCount(Integer.parseInt(getValue().toString())));
                                    }
                                };

                                add(skeletonsLabel, BorderLayout.WEST);
                                add(skeletonsSpin, BorderLayout.EAST);
                            }
                        };

                        // towers:
                        JPanel redTowersPane = new JPanel(new BorderLayout()) {
                            {
                                JLabel redTowersLabel = new JLabel("Red towers");

                                JSpinner redTowersSpin = new JSpinner() {
                                    {
                                        setValue(parent.getRedTowerCount());

                                        addChangeListener(_ -> parent.setRedTowerCount(Integer.parseInt(getValue().toString())));
                                    }
                                };

                                add(redTowersLabel, BorderLayout.WEST);
                                add(redTowersSpin, BorderLayout.EAST);
                            }
                        };

                        JPanel whiteTowersPane = new JPanel(new BorderLayout()) {
                            {
                                JLabel whiteTowersLabel = new JLabel("White towers");

                                JSpinner whiteTowersSpin = new JSpinner() {
                                    {
                                        setValue(parent.getWhiteTowerCount());

                                        addChangeListener(_ -> parent.setWhiteTowerCount(Integer.parseInt(getValue().toString())));
                                    }
                                };

                                add(whiteTowersLabel, BorderLayout.WEST);
                                add(whiteTowersSpin, BorderLayout.EAST);
                            }
                        };

                        JPanel greenTowersPane = new JPanel(new BorderLayout()) {
                            {
                                JLabel greenTowersLabel = new JLabel("Green towers");

                                JSpinner greenTowersSpin = new JSpinner() {
                                    {
                                        setValue(parent.getGreenTowerCount());

                                        addChangeListener(_ -> parent.setGreenTowerCount(Integer.parseInt(getValue().toString())));
                                    }
                                };

                                add(greenTowersLabel, BorderLayout.WEST);
                                add(greenTowersSpin, BorderLayout.EAST);
                            }
                        };

                        JPanel mageTowersPane = new JPanel(new BorderLayout()) {
                            {
                                JLabel mageTowersLabel = new JLabel("Mage towers");

                                JSpinner mageTowersSpin = new JSpinner() {
                                    {
                                        setValue(parent.getMageTowerCount());

                                        addChangeListener(_ -> parent.setMageTowerCount(Integer.parseInt(getValue().toString())));
                                    }
                                };

                                add(mageTowersLabel, BorderLayout.WEST);
                                add(mageTowersSpin, BorderLayout.EAST);
                            }
                        };

                        add(lifePane);
                        add(deathPane);
                        add(zombiesPane);
                        add(skeletonsPane);

                        add(redTowersPane);
                        add(whiteTowersPane);
                        add(greenTowersPane);
                        add(mageTowersPane);
                    }
                };

                add(spinersPane, BorderLayout.WEST);
            }
        };

        add(baseOptPane);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
