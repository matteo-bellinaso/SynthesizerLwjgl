package org.main.utils;

import org.main.synthetizerUI.SynthControlContainer;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static java.lang.Math.*;

public class Utils {

    public static class AudioInfo {

        public static final int SAMPLE_RATE = 44100;

    }

    public static void handleProcedure(Procedure procedure, boolean print) {
        try {
            procedure.invoke();
        } catch (Exception e) {
            if (print) {
                e.printStackTrace();
            }
        }
    }

    public static class parameterHandling {
        public static final Robot PARAMETER_ROBOT;

        static {
            try {
                PARAMETER_ROBOT = new Robot();
            } catch (AWTException e) {
                throw new ExceptionInInitializerError("Cannot construct robot instance");
            }
        }

        private parameterHandling() {
        }

        public static void addParameterMouseListener(Component component, SynthControlContainer controller,
                                                     int minVal, int maxVal, int valStep, RefWrapper<Integer> parameter,
                                                     Procedure onChangeProcedure) {
            component.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    final Cursor BLANK_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB),
                            new Point(0, 0), "blank_cursor");
                    component.setCursor(BLANK_CURSOR);
                    controller.setMouseCLickLocation(e.getLocationOnScreen());
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    component.setCursor(Cursor.getDefaultCursor());
                }
            });

            component.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (controller.getMouseCLickLocation().y != e.getYOnScreen()) {
                        boolean mouseMovingUp = controller.getMouseCLickLocation().y - e.getYOnScreen() > 0;
                        if (mouseMovingUp && parameter.val < maxVal) {
                            parameter.val += valStep;
                        } else if (!mouseMovingUp && parameter.val > minVal) {
                            parameter.val -= valStep;
                        }

                        if (onChangeProcedure != null) {
                            handleProcedure(onChangeProcedure, true);
                        }
                        PARAMETER_ROBOT.mouseMove(controller.getMouseCLickLocation().x, controller.getMouseCLickLocation().y);
                    }
                }
            });
        }
    }


    public static class WindowDesign {
        public static final Border LINE_BORDER = BorderFactory.createLineBorder(Color.BLACK);
    }

    public static class Math {
        public static double offsetTone(double baseFrequency, double frequenctMultiplier) {
            return baseFrequency * pow(2.0, frequenctMultiplier);
        }

        public static double frequencyToAngularFrequency(double freq) {
            return 2 * PI * freq;
        }

        public static double getKeyFrequency(int keyNum) {
            return pow(root(2, 12), keyNum - 49) * 440;
        }

        public static double root(double num, double root) {
            return pow(E, log(num) / root);
        }
    }
}
