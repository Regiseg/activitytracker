package week07d04;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShoppingListTest {

    @Test
    void calculateSumTest() {
        ShoppingList shoppingList = new ShoppingList();

        assertEquals(335, shoppingList.calculateSum("shopping_list.txt"));

    }
}