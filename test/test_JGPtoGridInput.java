import org.junit.*;
import util.JPGtoGridInput;
import java.io.*;
import java.util.*;

public class test_JGPtoGridInput {

    public List<Double> readTxtFile(String fileName) throws FileNotFoundException {
        Scanner in = new Scanner(new File(fileName));
        List<Double> result = new LinkedList<>();
        while (in.hasNextDouble()) {
            result.add(in.nextDouble());
        }
        return result;
    }

    @Test
    public void testGrid4x4() throws IOException {
        JPGtoGridInput testImg = new JPGtoGridInput("./input_data/test_img_4x4.jpeg");
        testImg.generateGridInput("./test/tmp_text.txt");
        List<Double> expect = readTxtFile("./test/banchmark_img_4x4.txt");
        List<Double> actual = readTxtFile("./test/tmp_text.txt");
        Assert.assertEquals(expect.size(), actual.size());
        Iterator<Double> expectIt = expect.iterator();
        Iterator<Double> actualIt = actual.iterator();
        while (expectIt.hasNext() && actualIt.hasNext()) {
            double expectValue = expectIt.next();
            double actualValue = actualIt.next();
            Assert.assertEquals(expectValue, actualValue, 0.0001);
        }
        File tmpFile = new File("./test/tmp_text.txt");
        tmpFile.delete();
    }
}
