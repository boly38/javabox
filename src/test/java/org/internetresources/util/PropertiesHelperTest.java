package org.internetresources.util;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Test;

public class PropertiesHelperTest {

    @Test
    public void should_be_able_to_get_array_of_properties() {
        System.setProperty("my.parameter.0", "ooO");
        System.setProperty("my.parameter.1", "oO");
        System.setProperty("my.parameter.2", "boo");
        System.setProperty("my.parameter.4", "after_hole");
        // WHEN
        String[] pluginParams = PropertiesHelper
                .getSystemStringProperties("my.parameter");

        // THEN
        assertThat(pluginParams).isNotNull();
        assertThat(pluginParams).containsExactly("ooO", "oO", "boo");
        System.out.println((Arrays.asList(pluginParams)).toString());
    }

}
