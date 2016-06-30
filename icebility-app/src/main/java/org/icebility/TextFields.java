package org.icebility;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class TextFields  {
	public final static void integerTextField(TextField tf) {
		validatedTextField(tf, "\\d*");
	}
	public final static void validatedTextField(final TextField tf, final String pattern) {
		tf.textProperty().addListener(new ChangeListener<String>() {
		    @Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		        if (!newValue.matches(pattern)) {
		            tf.setText(oldValue);
		        }
		    }
		});
	}
}