package core.utils;

import gui.ava.html.image.generator.HtmlImageGenerator;
import javax.swing.*;

public class HtmlImageGeneratorCustom extends HtmlImageGenerator {

    @Override
    protected JEditorPane createJEditorPane() {
        JEditorPane edit = super.createJEditorPane();
        edit.setEditable(false);
        CustomToolKit toolKit = new CustomToolKit();
        edit.setEditorKit(toolKit);
        return edit;
    }
}
