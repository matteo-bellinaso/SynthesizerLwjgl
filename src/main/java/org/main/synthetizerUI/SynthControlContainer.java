package org.main.synthetizerUI;

import javax.swing.*;
import java.awt.*;

public class SynthControlContainer  extends JPanel {

    protected  boolean on;
    private SynthetizerRemastered synthetizerRemastered;
    private Point mouseCLickLocation;

    public SynthControlContainer(SynthetizerRemastered synth) {
        this.synthetizerRemastered = synth;
    }

    public Point getMouseCLickLocation() {
        return mouseCLickLocation;
    }

    public void setMouseCLickLocation(Point location) {
        this.mouseCLickLocation = location;
    }

    public boolean isOn(){
        return  on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    @Override
    public Component add(Component component) {
        component.addKeyListener(synthetizerRemastered.getKeyAdapter());
        return super.add(component);
    }

    @Override
    public Component add(Component component, int index) {
        component.addKeyListener(synthetizerRemastered.getKeyAdapter());
        return super.add(component, index);
    }

    @Override
    public Component add(String name, Component component) {
        component.addKeyListener(synthetizerRemastered.getKeyAdapter());
        return super.add(name, component);
    }

    @Override
    public void add(Component component, Object constraits) {
        component.addKeyListener(synthetizerRemastered.getKeyAdapter());
        super.add(component, constraits);
    }

    @Override
    public void add(Component component, Object constraits, int index) {
        component.addKeyListener(synthetizerRemastered.getKeyAdapter());
        super.add(component, constraits, index);
    }
}
