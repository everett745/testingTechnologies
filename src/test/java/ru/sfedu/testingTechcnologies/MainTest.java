package ru.sfedu.testingTechcnologies;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Log4j2
public class MainTest {
    @Test
    public void test() {
        System.out.println(123123);
        assertTrue(true);
    }

    @Test
    public void test2() {
        assertTrue(true);
    }
}
