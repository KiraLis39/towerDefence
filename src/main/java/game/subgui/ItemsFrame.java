package game.subgui;

import fox.FoxFontBuilder;
import game.buildings.BuildBase;
import game.config.Registry;
import game.decorations.DecorBase;
import game.gui.GameFrame;
import game.objects.AbstractBuilding;
import game.objects.AbstractDecor;
import lombok.Getter;
import lombok.Setter;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

@Getter
public class ItemsFrame extends JFrame {
    private final List<Point2D> newGreenKeys = new LinkedList<>();
    public Boolean addPointToolIsSelected = false, remPointToolIsSelected = false, freeMouse = true, defSquareToolIsSelected = false;
    private JFrame itemsFrame;
    private JPanel content;
    private JPanel newMapNamer;
    private JTextField addressArea;

    @Getter
    @Setter
    private int lives, deaths, zombiesCount, skeletonsCount, redTowerCount, whiteTowerCount, greenTowerCount, mageTowerCount;


    public ItemsFrame() {
        itemsFrame = this;

        lives = levelManager.getCurrentLifesCount();
        deaths = levelManager.getCurrentDethsCount();

        zombiesCount = levelManager.getZombieCount();
        skeletonsCount = levelManager.getSkeletonCount();

        redTowerCount = levelManager.getTowersCount(0);
        whiteTowerCount = levelManager.getTowersCount(1);
        greenTowerCount = levelManager.getTowersCount(2);
        mageTowerCount = levelManager.getTowersCount(3);

        setTitle("Items pack:");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(250, 250));
        setLayout(new BorderLayout());
        setAlwaysOnTop(true);

        newMapNamer = new JPanel(new GridLayout(0, 1, 3, 3)) {
            {
                setBorder(new EmptyBorder(3, 3, 3, 3));

                addressArea = new JTextField("map_" + String.valueOf(System.currentTimeMillis()).substring(5, 10)) {
                    {
                        setBackground(Color.DARK_GRAY);
                        setForeground(Color.GREEN);
                        setFont(Registry.ffb.setFoxFont(FoxFontBuilder.FONT.ARIAL_NARROW, 20, false));
                        setBorder(new EmptyBorder(3, 5, 3, 5));
                        setHorizontalAlignment(CENTER);
                    }
                };

                File[] backgrounds = new File("resources/pic/maps/").listFiles();
                DefaultComboBoxModel<String> cbModel = new DefaultComboBoxModel<String>();
                assert backgrounds != null;
                for (File background : backgrounds) {
                    cbModel.addElement(background.getName().replace(".png", ""));
                }
                JComboBox<String> backgroundChoiser = new JComboBox<>(cbModel) {
                    {
                        addActionListener(e -> {
                            System.out.println("Chosen back: " + getSelectedItem());
                            levelManager.setCurrentBackImage(backgrounds[getSelectedIndex()]);
                        });
                    }
                };

                add(addressArea);
                add(backgroundChoiser);
            }
        };

        content = new JPanel(new GridLayout(0, 3, 3, 3)) {
            {
                setName("itemsContentPane");
                setBackground(Color.GRAY);
                setFocusable(true);

                addCommandButtons();

                addElementsButtons();

                addMouseListener(GameFrame.getMList());
            }

            private void addCommandButtons() {
                add(new JButton() {
                    {
                        setIcon(new ImageIcon("./resources/pic/mapbuilder/saveCurMap.png"));
                        setFocusPainted(false);
                        setBorderPainted(false);
                        setBackground(Color.BLUE.brighter());
                        setToolTipText("Сохранить текущую карту.");
                        addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                addPointToolIsSelected = false;
                                remPointToolIsSelected = false;
                                defSquareToolIsSelected = false;

                                setCursor(Registry.cur_0);
                                GameFrame.setsCursor(Registry.cur_0);

                                if (addressArea.getText().isEmpty()) {
                                    JOptionPane.showConfirmDialog(addressArea,
                                            "Имя новой карты слишком короткое!", "Ошибка:",
                                            JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
                                    return;
                                }

                                if (new File(Registry.levelsDir + "/" + addressArea.getText() + ".map").exists()) {
                                    int req = JOptionPane.showConfirmDialog(addressArea,
                                            "<HTML>Карта с таким именем уже имеется!<BR>Перезаписать?", "Внимание:",
                                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

                                    if (req == 2) {
                                        return;
                                    }
                                }

                                levelManager.saveLevel(
                                        addressArea.getText(),
                                        lives, deaths,
                                        zombiesCount, skeletonsCount,
                                        newGreenKeys,
                                        redTowerCount, whiteTowerCount, greenTowerCount, mageTowerCount);
                            }
                        });
                        addMouseMotionListener(GameFrame.getMMList());
                    }
                });

                add(new JButton() {
                    {
                        setIcon(new ImageIcon("./resources/pic/mapbuilder/wayPointerToolAdd.png"));
                        setFocusPainted(false);
                        setBorderPainted(false);
                        setBackground(Color.GREEN);
                        setToolTipText("Добавить новую точку пути.");
                        addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                resetBrush();
                                addPointToolIsSelected = true;
                                remPointToolIsSelected = false;
                                defSquareToolIsSelected = false;
                                setCursor(Registry.addPoint);
                                GameFrame.setsCursor(Registry.addPoint);
                            }
                        });
                        addMouseMotionListener(GameFrame.getMMList());
                    }
                });

                add(new JButton() {
                    {
                        setIcon(new ImageIcon("./resources/pic/mapbuilder/wayPointerToolRem.png"));
                        setFocusPainted(false);
                        setBorderPainted(false);
                        setBackground(Color.MAGENTA.darker());
                        setToolTipText("Удалить объект. Осторожно!");
                        addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                resetBrush();
                                addPointToolIsSelected = false;
                                remPointToolIsSelected = true;
                                defSquareToolIsSelected = false;
                                setCursor(Registry.remPoint);
                                GameFrame.setsCursor(Registry.remPoint);
                            }
                        });
                        addMouseMotionListener(GameFrame.getMMList());
                    }
                });

                add(new JButton() {
                    {
                        setIcon(new ImageIcon("./resources/pic/mapbuilder/clearMap.png"));
                        setFocusPainted(false);
                        setBorderPainted(false);
                        setBackground(Color.RED.darker());
                        setToolTipText("Сбросить/удалить карту.");
                        addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                int req = JOptionPane.showConfirmDialog(newMapNamer, "Точно очистить?", "Подтвердить:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

                                if (req == 0) {
                                    clearCreativeFields();
                                } else {
                                    System.out.println("Clearing was canselled by user.");
                                }
                            }
                        });
                        addMouseMotionListener(GameFrame.getMMList());
                    }
                });

                add(new JButton() {
                    {
                        setIcon(new ImageIcon("./resources/pic/mapbuilder/optionsCreate.png"));
                        setFocusPainted(false);
                        setBorderPainted(false);
                        setBackground(Color.YELLOW.darker());
                        setToolTipText("Опции уровня.");
                        addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                new CreateLevelOptions(ItemsFrame.this);
                            }
                        });
                        addMouseMotionListener(GameFrame.getMMList());
                    }
                });

                add(new JButton() {
                    {
                        setIcon(new ImageIcon("./resources/pic/mapbuilder/defenceSquare.png"));
                        setFocusPainted(false);
                        setBorderPainted(false);
                        setBackground(Color.CYAN.darker());
                        setToolTipText("Здание защиты.");
                        addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                resetBrush();
                                addPointToolIsSelected = false;
                                remPointToolIsSelected = false;
                                defSquareToolIsSelected = true;
                                GameFrame.setsCursor(Registry.defPoint);
                            }
                        });
                        addMouseMotionListener(GameFrame.getMMList());
                    }
                });
            }

            private void addElementsButtons() {
                for (AbstractDecor elem : DecorBase.getAll()) {
                    add(new JButton() {
                        {
                            setName("itemButton_" + elem.getID());
                            Image ico = elem.getIcon().getImage();
                            if (ico.getWidth(null) > 64) {
                                ico = ico.getScaledInstance(64, 64, 2);
                            }
                            setIcon(new ImageIcon(ico));
                            setFocusPainted(false);
                            setBorderPainted(false);
                            setBackground(Color.GRAY);
                            addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    addPointToolIsSelected = false;
                                    remPointToolIsSelected = false;
                                    defSquareToolIsSelected = false;
//									setsCursor(Cursors.getPNGCursor(elem.getIcon(), "cur0"));
//									GameLevel.setsCursor(Cursors.getPNGCursor("cur0"));
                                }
                            });
                            addMouseListener(GameFrame.getMList());
                            addMouseMotionListener(GameFrame.getMMList());
                        }

                        @Override
                        public void paintComponent(Graphics g) {
                            super.paintComponent(g);
                            g.drawString(String.valueOf(elem.getID()), 6, getWidth() - 25);
                            g.dispose();
                            setToolTipText(elem.getComment());
                        }
                    });
                }

                for (AbstractBuilding elem : BuildBase.getAll()) {
                    add(new JButton() {
                        {
                            setName("itemButton_" + elem.getID());
                            Image ico = elem.getIcon().getImage();
                            if (ico.getWidth(null) > 64) {
                                ico = ico.getScaledInstance(64, 64, 2);
                            }
                            setIcon(new ImageIcon(ico));
                            setToolTipText("ПКМ для сброса кисти.");
                            setFocusPainted(false);
                            setBorderPainted(false);
                            setBackground(Color.GRAY);
                            addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    addPointToolIsSelected = false;
                                    remPointToolIsSelected = false;
                                    defSquareToolIsSelected = false;
                                }
                            });
                            addMouseListener(GameFrame.getMList());
                            addMouseMotionListener(GameFrame.getMMList());
                        }
                    });
                }
            }
        };

        add(newMapNamer, BorderLayout.NORTH);
        add(new JScrollPane(content), BorderLayout.CENTER);

        addMouseMotionListener(GameFrame.getMMList());

        pack();
        setVisible(true);
    }

    public void clearCreativeFields() {
        resetBrush();

        addPointToolIsSelected = false;
        remPointToolIsSelected = false;
        defSquareToolIsSelected = false;

        levelManager.clearCurrentLevel();
    }

    public void resetBrush() {
        freeMouse = true;
        GameFrame.resetMemoryImage();
        GameFrame.resetMemoryDecorID();

        Cursor cur = Registry.cur_0;
        if (cur == null) {
            throw new RuntimeException("ItemsFrame (resetBrush): Cursor is NULL!");
        }
        if (itemsFrame == null) {
//			throw new RuntimeException("itemsFrame (resetBrush): itemsFrame is NULL!");
            GameFrame.getOwnFrame().setCursor(cur);
            return;
        }
        itemsFrame.setCursor(cur);
        GameFrame.setsCursor(Registry.cur_0);
    }

    public void addGreenKey(Point point) {
        newGreenKeys.add(point);
    }

    public void removeGreenKey(Point2D p) {
        newGreenKeys.remove(p);
    }
}
