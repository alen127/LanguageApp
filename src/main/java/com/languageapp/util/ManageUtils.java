package com.languageapp.util;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ManageUtils {
    public static String getRequiredFieldsMessage(List<Control> fields, List<String> fieldNames) {
        StringBuilder msg = new StringBuilder();
        LinkedHashMap<Control, String> fieldToFieldNameMap = IntStream.range(0, fields.size()).boxed()
                .collect(Collectors.toMap(fields::get, fieldNames::get, (e1, e2) -> e1, LinkedHashMap::new));
        for (Map.Entry<Control, String> entry : fieldToFieldNameMap.entrySet()) {
            if (entry.getKey() instanceof TextField textField) {
                if (textField.getText().isBlank())
                    msg.append(entry.getValue()).append(" is required.\n");
            } else if (entry.getKey() instanceof ChoiceBox<?> choiceBox) {
                if (choiceBox.getValue() == null)
                    msg.append(entry.getValue()).append(" is required.\n");
            }
        }
        return msg.toString();
    }
}
