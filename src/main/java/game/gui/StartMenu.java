package game.gui;

import game.config.Registry;
import game.core.FoxAudioProcessor;
import game.core.SavesEngine;
import game.subgui.OptionsDialog;
import lombok.extern.slf4j.Slf4j;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;


@Slf4j
public class StartMenu extends JFrame implements MouseListener, MouseMotionListener {
    private final Canvas canva;
    private final Rectangle2D[] menuButRects = new Rectangle2D.Float[4];
    private float widthPercent, heightPercent;
    private boolean cmodeUnlocked, isContinueOver = false, isContinuePress = false, isNewGameOver = false, isNewGamePress = false,
            isOptionsOver = false, isOptionsPress = false, isExitOver = false, isExitPress = false, doBreak = false;
    private BufferStrategy bs;
    private Point mousePressPoint;
    private BufferedImage[] buts;
    private BufferedImage menuPicture01, guiBackground;


    public StartMenu() {
        init();

        canva = new Canvas(getGraphicsConfiguration());
        canva.addMouseListener(this);
        canva.addMouseMotionListener(this);

        add(canva);

        inAcInit();
        postInit();

        Thread.startVirtualThread(() -> {
            while (!doBreak) {
                try {
                    bs = canva.getBufferStrategy();
                    update();
                    Thread.sleep(1000 / 60);
                } catch (Exception e) {
                    log.error("Ошибка при обновлении холста игры: {}", e.getMessage());
                }
            }
        });
    }

    private void postInit() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        canva.createBufferStrategy(2);

        menuButRects[0] = new Rectangle2D.Float(getWidth() / 2f - widthPercent * 15f,
                heightPercent * 20f, widthPercent * 30f, heightPercent * 10f);
        menuButRects[1] = new Rectangle2D.Float(getWidth() / 2f - widthPercent * 15f,
                heightPercent * 32f, widthPercent * 30f, heightPercent * 10f);
        menuButRects[2] = new Rectangle2D.Float(getWidth() / 2f - widthPercent * 15f,
                heightPercent * 44f, widthPercent * 30f, heightPercent * 10f);
        menuButRects[3] = new Rectangle2D.Float(getWidth() / 2f - widthPercent * 15f,
                heightPercent * 56f, widthPercent * 30f, heightPercent * 10f);
    }

    private void init() {
        FoxAudioProcessor.stopMusic();

        cmodeUnlocked = Registry.userConf.isCreativeMode();
        buts = Registry.sComb.getSprites("defButs", Registry.cache.getBufferedImage("defaultButtons"), 3, 1);
        assert buts != null;

        menuPicture01 = Registry.cache.getBufferedImage("menuPicture_1");
        guiBackground = Registry.cache.getBufferedImage("gui_background");

        setCursor(Registry.cur_0);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setState(NORMAL);
        setUndecorated(false);
        setPreferredSize(new Dimension(
                Integer.parseInt((String) Registry.props.get("width")),
                Integer.parseInt((String) Registry.props.get("height"))));

        widthPercent = Integer.parseInt((String) Registry.props.get("width")) / 100f;
        heightPercent = Integer.parseInt((String) Registry.props.get("height")) / 100f;
    }

    private void inAcInit() {
        Registry.inAc.add("menu", getRootPane());
        Registry.inAc.set("menu", "exit", KeyEvent.VK_ESCAPE, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SavesEngine.saveAll();
                System.exit(0);
            }
        });
        Registry.inAc.set("menu", "creatorMode", KeyEvent.VK_C, 1, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cmodeUnlocked) {
                    dispose();
                    try {
                        new GameFrame(139, false);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
    }


    private void rendering(Graphics2D g2D) {
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
//		if (useSmoothing) {
//			if (useBicubic) {g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//			} else {g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);}
//		} else {g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);}
//		g2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
//		g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//		g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    protected void update() {
        do {
            do {
                try {
                    Graphics2D g2D = (Graphics2D) bs.getDrawGraphics();
                    rendering(g2D);
                    updateCanvas(g2D);
                } catch (Exception e) {
                    log.error("Error: {}", e.getMessage());
                }
            } while (bs.contentsLost());
        } while (bs.contentsRestored());
        bs.show();
    }

    private void updateCanvas(Graphics2D g2D) {
        g2D.drawImage(guiBackground, 0, 0, getWidth(), getHeight(), this);

        g2D.setFont(Registry.titleFont);
        g2D.setColor(Color.ORANGE);
        g2D.drawString("Tower Defense v." + Registry.props.get("version"), widthPercent * 2f - 3f, heightPercent * 3.5f + 3f);

        if (cmodeUnlocked) {
            g2D.drawString("SHIFT + C", getWidth() - 100f, heightPercent * 3.5f + 3f);
        }

        g2D.drawImage(menuPicture01, (int) (widthPercent * 1.8f), (int) (heightPercent * 6f), (int) (widthPercent * 96.4f), (int) (heightPercent * 92f), this);

        int var0 = 0;
        if (isContinueOver) {
            if (isContinuePress) {
                var0 = 2;
            } else {
                var0 = 1;
            }
        }
        Composite comp = g2D.getComposite();
        g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
        g2D.drawImage(buts[var0],
                menuButRects[0].getBounds().x, menuButRects[0].getBounds().y,
                menuButRects[0].getBounds().width, menuButRects[0].getBounds().height, this);

        int var1 = 0;
        if (isNewGameOver) {
            if (isNewGamePress) {
                var1 = 2;
            } else {
                var1 = 1;
            }
        }
        g2D.drawImage(buts[var1],
                menuButRects[1].getBounds().x, menuButRects[1].getBounds().y,
                menuButRects[1].getBounds().width, menuButRects[1].getBounds().height, this);

        int var2 = 0;
        if (isOptionsOver) {
            if (isOptionsPress) {
                var2 = 2;
            } else {
                var2 = 1;
            }
        }
        g2D.drawImage(buts[var2],
                menuButRects[2].getBounds().x, menuButRects[2].getBounds().y,
                menuButRects[2].getBounds().width, menuButRects[2].getBounds().height, this);

        int var3 = 0;
        if (isExitOver) {
            if (isExitPress) {
                var3 = 2;
            } else {
                var3 = 1;
            }
        }
        g2D.drawImage(buts[var3],
                menuButRects[3].getBounds().x, menuButRects[3].getBounds().y,
                menuButRects[3].getBounds().width, menuButRects[3].getBounds().height, this);

        g2D.setComposite(comp);

        // buttons text:
        int centerFrameWidth = getWidth() / 2, shiftMod;
        g2D.setFont(Registry.buttonsFont);


        g2D.setColor(Color.DARK_GRAY);
        if (isContinueOver) {
            shiftMod = 2;
        } else {
            shiftMod = 0;
        }
        g2D.drawString("Продолжить",
                (int) (centerFrameWidth - Registry.ffb.getStringBounds(g2D, "Продолжить").getWidth() / 2 - shiftMod - 3),
                (int) ((menuButRects[0].getY() + (menuButRects[0].getHeight() / 2) + Registry.ffb.getStringBounds(g2D, "Продолжить").getMaxY() / 2) + shiftMod + 3));

        if (isNewGameOver) {
            shiftMod = 2;
        } else {
            shiftMod = 0;
        }
        g2D.drawString("Новая игра",
                (int) (centerFrameWidth - Registry.ffb.getStringBounds(g2D, "Новая игра").getWidth() / 2 - shiftMod - 3),
                (int) ((menuButRects[1].getY() + (menuButRects[1].getHeight() / 2) + Registry.ffb.getStringBounds(g2D, "Новая игра").getMaxY() / 2) + shiftMod + 3));

        if (isOptionsOver) {
            shiftMod = 2;
        } else {
            shiftMod = 0;
        }
        g2D.drawString("Настройки",
                (int) (centerFrameWidth - Registry.ffb.getStringBounds(g2D, "Настройки").getWidth() / 2 - shiftMod - 3),
                (int) ((menuButRects[2].getY() + (menuButRects[2].getHeight() / 2) + Registry.ffb.getStringBounds(g2D, "Настройки").getMaxY() / 2) + shiftMod + 3));

        if (isExitOver) {
            shiftMod = 2;
        } else {
            shiftMod = 0;
        }
        g2D.drawString("Выход",
                (int) (centerFrameWidth - Registry.ffb.getStringBounds(g2D, "Выход").getWidth() / 2 - shiftMod - 3),
                (int) ((menuButRects[3].getY() + (menuButRects[3].getHeight() / 2) + Registry.ffb.getStringBounds(g2D, "Выход").getMaxY() / 2) + shiftMod + 3));


        g2D.setColor(Color.ORANGE);
        if (isContinueOver) {
            shiftMod = 2;
        } else {
            shiftMod = 0;
        }
        g2D.drawString("Продолжить",
                (int) (centerFrameWidth - Registry.ffb.getStringBounds(g2D, "Продолжить").getWidth() / 2 - shiftMod),
                (int) (menuButRects[0].getY() + (menuButRects[0].getHeight() / 2) + Registry.ffb.getStringBounds(g2D, "Продолжить").getMaxY() / 2) + shiftMod);

        if (isNewGameOver) {
            shiftMod = 2;
        } else {
            shiftMod = 0;
        }
        g2D.drawString("Новая игра",
                (int) (centerFrameWidth - Registry.ffb.getStringBounds(g2D, "Новая игра").getWidth() / 2 - shiftMod),
                (int) (menuButRects[1].getY() + (menuButRects[1].getHeight() / 2) + Registry.ffb.getStringBounds(g2D, "Новая игра").getMaxY() / 2) + shiftMod);

        if (isOptionsOver) {
            shiftMod = 2;
        } else {
            shiftMod = 0;
        }
        g2D.drawString("Настройки",
                (int) (centerFrameWidth - Registry.ffb.getStringBounds(g2D, "Настройки").getWidth() / 2 - shiftMod),
                (int) (menuButRects[2].getY() + (menuButRects[2].getHeight() / 2) + Registry.ffb.getStringBounds(g2D, "Настройки").getMaxY() / 2) + shiftMod);

        if (isExitOver) {
            shiftMod = 2;
        } else {
            shiftMod = 0;
        }
        g2D.drawString("Выход",
                (int) (centerFrameWidth - Registry.ffb.getStringBounds(g2D, "Выход").getWidth() / 2 - shiftMod),
                (int) (menuButRects[3].getY() + (menuButRects[3].getHeight() / 2) + Registry.ffb.getStringBounds(g2D, "Выход").getMaxY() / 2) + shiftMod);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (isContinueOver || isNewGameOver || isOptionsOver || isExitOver) {
            return;
        }

        setLocation(getLocation().x + (e.getX() - mousePressPoint.x), getLocation().y + (e.getY() - mousePressPoint.y));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (menuButRects[0] == null) {
            return;
        }

        if (menuButRects[0].contains(e.getPoint())) {
            if (!isContinueOver) {
                FoxAudioProcessor.playSound("sButtonOver");
            }
            isContinueOver = true;
        } else {
            isContinueOver = false;
        }

        if (menuButRects[1].contains(e.getPoint())) {
            if (!isNewGameOver) {
                FoxAudioProcessor.playSound("sButtonOver");
            }
            isNewGameOver = true;
        } else {
            isNewGameOver = false;
        }

        if (menuButRects[2].contains(e.getPoint())) {
            if (!isOptionsOver) {
                FoxAudioProcessor.playSound("sButtonOver");
            }
            isOptionsOver = true;
        } else {
            isOptionsOver = false;
        }

        if (menuButRects[3].contains(e.getPoint())) {
            if (!isExitOver) {
                FoxAudioProcessor.playSound("sButtonOver");
            }
            isExitOver = true;
        } else {
            isExitOver = false;
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (menuButRects[0] == null) {
            return;
        }

        mousePressPoint = e.getPoint();

        if (menuButRects[0].contains(e.getPoint())) {
            isContinuePress = true;
        }
        if (menuButRects[1].contains(e.getPoint())) {
            isNewGamePress = true;
        }
        if (menuButRects[2].contains(e.getPoint())) {
            isOptionsPress = true;
        }
        if (menuButRects[3].contains(e.getPoint())) {
            isExitPress = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (menuButRects[0] == null) {
            return;
        }

        if (menuButRects[0].contains(e.getPoint())) {
            isContinuePress = false;
            log.info("Game continue here...");

        }

        if (menuButRects[1].contains(e.getPoint())) {
            isNewGamePress = false;

            dispose();

            try {
                log.info("Start a New game...");
                new GameFrame();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        if (menuButRects[2].contains(e.getPoint())) {
            isOptionsPress = false;
            log.info("Open the options dialog...");
            new OptionsDialog(StartMenu.this, true);
        }

        if (menuButRects[3].contains(e.getPoint())) {
            isExitPress = false;
            log.info("Exit by press Exit button...");

            SavesEngine.saveAll();
            System.exit(0);
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
