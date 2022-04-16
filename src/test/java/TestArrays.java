import com.geekbrains.arrays.MyArrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class TestArrays {

    private MyArrays myArrays;

    @BeforeEach
    public void init() {
        myArrays = new MyArrays();
    }


    @Test
    public void testGetAllAfterLast4_Good() {
        Integer[] myInt = myArrays.GetAllAfterLast4(new Integer[]{1, 4, 3, 5, 4, 7, 2});
        Integer[] GoodResult = new Integer[]{7,2};
        Assertions.assertTrue(Arrays.equals(myInt,GoodResult));
    }

    @Test
    public void testGetAllAfterLast4_Bad() {
        Integer[] myInt = myArrays.GetAllAfterLast4(new Integer[]{7,5,2,4,3,9,4,1});
        Integer[] BadResult = new Integer[]{3,9};
        Assertions.assertFalse(Arrays.equals(myInt,BadResult));
    }

    @Test
    public void testGetAllAfterLast4_RuntimeException() {
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            myArrays.GetAllAfterLast4(new Integer[]{9,6,1,9});
        });

        String expectedMessage = "В массиве нет ни одной 4-ки!";
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.equals(expectedMessage));
    }

    @Test
    public void testCheckArray1And4_False() {
        Assertions.assertFalse(myArrays.CheckArray1And4(new Integer[]{1,1,0,0,0,1,0}));
    }

    @Test
    public void testCheckArray1And4_False2() {
        Assertions.assertFalse(myArrays.CheckArray1And4(new Integer[]{4,4,4,4,4,2,2,2}));
    }

    @Test
    public void testCheckArray1And4_True() {
        Assertions.assertTrue(myArrays.CheckArray1And4(new Integer[]{4,4,4,1,1,1,1,1,4,4}));
    }

    @Test
    public void testCheckArray1And4_True2() {
        Assertions.assertTrue(myArrays.CheckArray1And4(new Integer[]{1,1,1,1,1,4}));
    }

}
