package com.alee.laf.text;

import javax.swing.plaf.basic.BasicTextUI;
import javax.swing.text.JTextComponent;

/**
 * Base interface for text area component painters.
 *
 * @author Alexandr Zernov
 */

public interface IAbstractTextAreaPainter<E extends JTextComponent, U extends BasicTextUI> extends IAbstractTextEditorPainter<E, U>
{
}