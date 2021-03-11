package week07d03;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DateTest {

    @Test
    void ofTest() {
        Date date = Date.of(2012, 3,15);
        assertEquals(2012, date.getYear());
        assertEquals(3, date.getMonth());
        assertEquals(15, date.getDay());
    }

    @Test
    void withYearTest() {
        Date date = Date.of(2012, 3,15);
        Date newDate = date.withYear(2020);
        assertEquals(2020, newDate.getYear());
        assertEquals(3, newDate.getMonth());
        assertEquals(15, newDate.getDay());
    }

    @Test
    void withMonth() {
        Date date = Date.of(2012, 3,15);
        Date newDate = date.withMonth(8);
        assertEquals(2012, newDate.getYear());
        assertEquals(8, newDate.getMonth());
        assertEquals(15, newDate.getDay());
    }

    @Test
    void withDay() {
        Date date = Date.of(2012, 3,15);
        Date newDate = date.withDay(21);
        assertEquals(2012, newDate.getYear());
        assertEquals(3, newDate.getMonth());
        assertEquals(21, newDate.getDay());
    }
}