package com.tw0far.potiongames.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class SetupInputValidatorTest {
    @Test
    void acceptsPositiveLobbyIds() {
        assertEquals(1, SetupInputValidator.parseLobbyId("1"));
        assertEquals(27, SetupInputValidator.parseLobbyId("27"));
        assertEquals(42, SetupInputValidator.parseLobbyId("  42  "));
    }

    @Test
    void rejectsZeroAndNegativeLobbyIds() {
        assertNull(SetupInputValidator.parseLobbyId("0"));
        assertNull(SetupInputValidator.parseLobbyId("-1"));
        assertNull(SetupInputValidator.parseLobbyId("-27"));
    }

    @Test
    void rejectsNonNumericAndEmptyInput() {
        assertNull(SetupInputValidator.parseLobbyId("arena"));
        assertNull(SetupInputValidator.parseLobbyId("1.5"));
        assertNull(SetupInputValidator.parseLobbyId(""));
        assertNull(SetupInputValidator.parseLobbyId("   "));
        assertNull(SetupInputValidator.parseLobbyId(null));
    }

    @Test
    void rejectsValuesOutsideTheIntegerRange() {
        assertNull(SetupInputValidator.parseLobbyId("2147483648"));
        assertNull(SetupInputValidator.parseLobbyId("-2147483649"));
    }
}
