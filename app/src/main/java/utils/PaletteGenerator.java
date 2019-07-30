package utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.palette.graphics.Palette;

import java.io.File;

public class PaletteGenerator {

    public String generatePaletteColorsFromPath(String path) {
        // Use preferred ARGB_8888 decoding for 4 byte pixels
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        // Generate bitmap from the file path
        File f = new File(path);
        Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), options);

        // Generate palette, get colors
        Palette p = createPaletteSync(bitmap);
        return getColorsFromPalette(p);
    }

    // Get the colors from a given palette and assign them to a simple view
    private String getColorsFromPalette(Palette p) {
        int i = 0;
        StringBuilder colorString = new StringBuilder();

        for (Palette.Swatch s : p.getSwatches()) {
            if (i == 6) {
                break;
            } else if (i == 5) {
                colorString.append(s.getRgb());
            } else {
                colorString.append(s.getRgb()).append(",");
            }

            i++;
        }
        // Covers case where we find fewer than 6 palettes. We just copy the last one until we have 6.
        int index = i-1;
        while (i < 6) {
            if(i == 5) {
                colorString.append(p.getSwatches().get(index).getRgb());
            } else {
                colorString.append(p.getSwatches().get(index).getRgb()).append(",");
            }
            i++;
        }

        return colorString.toString();
    }

    // Generate palette synchronously and return it
    private Palette createPaletteSync(Bitmap bitmap) {
        return Palette.from(bitmap).generate();
    }
}
