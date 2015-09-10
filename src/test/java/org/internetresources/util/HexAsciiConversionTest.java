package org.internetresources.util;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

public class HexAsciiConversionTest {

    @Test
    public void should_convert_ascii_to_hex() {
        // GIVEN
        String sample = "blahblah george";
        // WHEN
        String hexVal = new HexAsciiConversion().asciiToHex(sample);
        //THEN
        assertThat(hexVal).isEqualTo("626c6168626c61682067656f726765");
    }

    @Test
    public void should_convert_hex_to_ascii() {
        // GIVEN
        String sample = "626c6168626c61682067656f726765";
        // WHEN
        String asciiVal = new HexAsciiConversion().hexToASCII(sample);
        //THEN
        assertThat(asciiVal).isEqualTo("blahblah george");
    }

}
