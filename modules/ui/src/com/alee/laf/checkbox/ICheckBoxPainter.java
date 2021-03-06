package com.alee.laf.checkbox;

import com.alee.laf.radiobutton.IAbstractStateButtonPainter;

import javax.swing.*;

/**
 * Base interface for JCheckBox component painters.
 *
 * @author Alexandr Zernov
 */

public interface ICheckBoxPainter<E extends JCheckBox, U extends WebCheckBoxUI> extends IAbstractStateButtonPainter<E, U>
{
}