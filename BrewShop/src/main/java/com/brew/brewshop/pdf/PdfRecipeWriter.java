package com.brew.brewshop.pdf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.brew.brewshop.R;
import com.brew.brewshop.settings.Settings;
import com.brew.brewshop.storage.recipes.HopAddition;
import com.brew.brewshop.storage.recipes.MaltAddition;
import com.brew.brewshop.storage.recipes.Recipe;
import com.brew.brewshop.storage.recipes.Weight;
import com.brew.brewshop.storage.recipes.Yeast;
import com.brew.brewshop.util.IngredientInfo;
import com.brew.brewshop.util.UnitConverter;
import com.brew.brewshop.util.Util;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PdfRecipeWriter {
    private static final String TAG = PdfRecipeWriter.class.getName();

    private static final int NORMAL_POINT = 12;
    private static final Font TITLE_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 36, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.TIMES_ROMAN, NORMAL_POINT, Font.NORMAL);
    private static final Font SMALL_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL, new BaseColor(64, 64, 64));
    private static final Font SPACING_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 6);

    private Recipe mRecipe;
    private Context mContext;
    private UnitConverter mConverter;
    private Settings mSettings;

    //NOTE: There are 72 units per inch

    public PdfRecipeWriter(Context context, Recipe recipe) {
        mContext = context;
        mRecipe = recipe;
        mConverter = new UnitConverter(context);
        mSettings = new Settings(context);
    }

    public void write(OutputStream outStream) {
        try {
            Document document = newDocument();
            PdfWriter.getInstance(document, outStream);
            document.open();
            addMetaData(document);
            addHeader(document);
            addRecipeInfo(document);
            addIngredients(document);
            addNotes(document);
            document.close();
        } catch (Exception e) {
            Log.e(TAG, "Error writing PDF", e);
        } finally {
            try {
                outStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing stream", e);
            }
        }
    }

    private Document newDocument() {
        Document document = new Document();
        document.setPageSize(PageSize.LETTER);
        document.setMargins(72, 72, 72, 72);
        return document;
    }

    private void addHeader(Document document) throws Exception {
        Image image = loadDrawable(R.drawable.ic_launcher, 26);
        Paragraph para = new Paragraph();
        para.setAlignment(Element.ALIGN_CENTER);
        para.setIndentationLeft(20);
        para.setIndentationRight(20);
        para.setSpacingBefore(0);
        para.setSpacingAfter(24);
        para.add(new Chunk(image, 0, 0));
        para.add(new Phrase(" ", TITLE_FONT));
        para.add(new Phrase(mContext.getString(R.string.brew_shop_recipe), TITLE_FONT));
        para.add(new Phrase(" ", TITLE_FONT));
        para.add(new Chunk(image, 0, 0));
        document.add(para);
    }

    private void addRecipeInfo(Document document) throws DocumentException {
        int iconRes = mRecipe.getStyle().getIconDrawable();
        Image image = loadDrawable(iconRes, 40);

        Paragraph namePara = new Paragraph();
        namePara.setSpacingBefore(0);
        namePara.add(new Phrase(mRecipe.getName() + "\n", HEADER_FONT));
        namePara.add(new Phrase("\n", new Font(Font.FontFamily.TIMES_ROMAN, 2)));
        namePara.add(new Phrase(mRecipe.getStyle().getDisplayName(), SMALL_FONT));

        PdfPTable table = new PdfPTable(new float[] {50, 400});
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        PdfPCell cellOne = new PdfPCell(image);
        cellOne.setBorder(Rectangle.NO_BORDER);
        PdfPCell cellTwo = new PdfPCell(namePara);
        cellTwo.setBorder(Rectangle.NO_BORDER);

        table.addCell(cellOne);
        table.addCell(cellTwo);
        document.add(table);

        addLineSeparator(document);

        Paragraph setupPara = new Paragraph();
        setupPara.add(new Phrase("Batch Volume: " + mConverter.fromGallonsWithUnits(mRecipe.getBatchVolume(), 1), NORMAL_FONT));
        setupPara.add(new Phrase("\n\n", SPACING_FONT));
        setupPara.add(new Phrase("Boil Volume: " + mConverter.fromGallonsWithUnits(mRecipe.getBoilVolume(), 1), NORMAL_FONT));
        setupPara.add(new Phrase("\n\n", SPACING_FONT));
        setupPara.add(new Phrase("Boil Time: " + Util.fromDouble(mRecipe.getBoilTime(), 0) + " minutes", NORMAL_FONT));
        setupPara.add(new Phrase("\n\n", SPACING_FONT));
        setupPara.add(new Phrase("Efficiency: " + Util.fromDouble(mRecipe.getEfficiency(), 1) + "%", NORMAL_FONT));

        Paragraph para = new Paragraph();
        String og = "";
        switch (mSettings.getExtractUnits()) {
            case SPECIFIC_GRAVITY:
                og = Util.fromDouble(mRecipe.getOg(), 3, false);
                break;
            case DEGREES_PLATO:
                og = Util.fromDouble(mRecipe.getOgPlato(), 1, false) + "°P";
                break;
        }
        para.add(new Phrase("OG: " + og, NORMAL_FONT));
        para.add(new Phrase("\n\n", SPACING_FONT));

        para.add(new Phrase("IBU: " + Util.fromDouble(mRecipe.getTotalIbu(), 1), NORMAL_FONT));
        para.add(new Phrase("\n\n", SPACING_FONT));
        para.add(new Phrase("SRM: " + Util.fromDouble(mRecipe.getSrm(), 1), NORMAL_FONT));
        para.add(new Phrase("\n\n", SPACING_FONT));

        String fg = "n/a";
        if (mRecipe.hasYeast()) {
            switch (mSettings.getExtractUnits()) {
                case SPECIFIC_GRAVITY:
                    fg = Util.fromDouble(mRecipe.getFg(), 3, false);
                    break;
                case DEGREES_PLATO:
                    fg = Util.fromDouble(mRecipe.getFgPlato(), 1, false) + "°P";
                    break;
            }
        }
        para.add(new Phrase("Estimated FG: " + fg, NORMAL_FONT));
        para.add(new Phrase("\n\n", SPACING_FONT));

        String abv = "n/a";
        if (mRecipe.hasYeast()) {
            abv = Util.fromDouble(mRecipe.getAbv(), 1) + "%";
        }
        para.add(new Phrase("Estimated ABV: " + abv, NORMAL_FONT));

        PdfPTable statsTable = new PdfPTable(new float[] {200, 200});
        statsTable.setHorizontalAlignment(Element.ALIGN_LEFT);
        PdfPCell statsCellOne = new PdfPCell(para);
        statsCellOne.setBorder(Rectangle.NO_BORDER);
        PdfPCell statsCellTwo = new PdfPCell(setupPara);
        statsCellTwo.setBorder(Rectangle.NO_BORDER);

        statsTable.addCell(statsCellOne);
        statsTable.addCell(statsCellTwo);
        document.add(statsTable);
    }

    private void addIngredients(Document document) throws DocumentException {
        Paragraph titlePara = new Paragraph(mContext.getString(R.string.ingredients), HEADER_FONT);
        document.add(titlePara);
        addLineSeparator(document);
        for (Object ingredient : mRecipe.getIngredients()) {
            if (ingredient instanceof MaltAddition) {
                addIngredient((MaltAddition) ingredient, document);
            } else if (ingredient instanceof HopAddition) {
                addIngredient((HopAddition) ingredient, document);
            } else if (ingredient instanceof Yeast) {
                addIngredient((Yeast) ingredient, document);
            }
            document.add(new Paragraph(" ", SPACING_FONT));
        }
    }

    private void addIngredient(MaltAddition malt, Document document) throws DocumentException {
        Image image = loadDrawable(R.drawable.barley_cap, 30);

        Paragraph namePara = new Paragraph();
        namePara.setSpacingBefore(0);
        namePara.add(new Phrase(formatWeight(malt.getWeight(), 2) + " ", NORMAL_FONT));
        namePara.add(new Phrase(malt.getMalt().getName() + "\n", NORMAL_FONT));
        namePara.add(new Phrase("\n", new Font(Font.FontFamily.TIMES_ROMAN, 2)));
        namePara.add(new Phrase(IngredientInfo.getInfo(malt, mRecipe), SMALL_FONT));

        PdfPTable table = new PdfPTable(new float[] {40, 400});
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        PdfPCell cellOne = new PdfPCell(image);
        cellOne.setBorder(Rectangle.NO_BORDER);
        PdfPCell cellTwo = new PdfPCell(namePara);
        cellTwo.setBorder(Rectangle.NO_BORDER);

        table.addCell(cellOne);
        table.addCell(cellTwo);
        document.add(table);
    }

    private void addIngredient(HopAddition hop, Document document) throws DocumentException {
        Image image = loadDrawable(R.drawable.hops_cap, 30);

        Paragraph namePara = new Paragraph();
        namePara.setSpacingBefore(0);
        namePara.add(new Phrase(formatWeight(hop.getWeight(), 3) + " ", NORMAL_FONT));
        namePara.add(new Phrase(hop.getHop().getName() + "\n", NORMAL_FONT));
        namePara.add(new Phrase("\n", new Font(Font.FontFamily.TIMES_ROMAN, 2)));
        namePara.add(new Phrase(IngredientInfo.getInfo(hop), SMALL_FONT));

        PdfPTable table = new PdfPTable(new float[] {40, 400});
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        PdfPCell cellOne = new PdfPCell(image);
        cellOne.setBorder(Rectangle.NO_BORDER);
        PdfPCell cellTwo = new PdfPCell(namePara);
        cellTwo.setBorder(Rectangle.NO_BORDER);

        table.addCell(cellOne);
        table.addCell(cellTwo);
        document.add(table);
    }

    private void addIngredient(Yeast yeast, Document document) throws DocumentException {
        Image image = loadDrawable(R.drawable.yeast_cap, 30);

        Paragraph namePara = new Paragraph();
        namePara.setSpacingBefore(0);
        namePara.add(new Phrase("1 Pkg. " + yeast.getName() + "\n", NORMAL_FONT));
        namePara.add(new Phrase("\n", new Font(Font.FontFamily.TIMES_ROMAN, 2)));
        namePara.add(new Phrase(IngredientInfo.getInfo(yeast), SMALL_FONT));

        PdfPTable table = new PdfPTable(new float[] {40, 400});
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        PdfPCell cellOne = new PdfPCell(image);
        cellOne.setBorder(Rectangle.NO_BORDER);
        PdfPCell cellTwo = new PdfPCell(namePara);
        cellTwo.setBorder(Rectangle.NO_BORDER);

        table.addCell(cellOne);
        table.addCell(cellTwo);
        document.add(table);
    }

    private void addNotes(Document document) throws DocumentException {
        if (!mRecipe.getNotes().equals("")) {
            document.add(new Paragraph(mContext.getString(R.string.notes), HEADER_FONT));
            addLineSeparator(document);
            Paragraph notesPara = new Paragraph(mRecipe.getNotes(), NORMAL_FONT);
            notesPara.setLeading(NORMAL_POINT);
            document.add(notesPara);
        }
    }

    private void addMetaData(Document document) {
        String title = mContext.getString(R.string.brew_shop_recipe_colon) + " " + mRecipe.getName();
        String author = mContext.getString(R.string.brew_shop);
        document.addTitle(title);
        document.addSubject(title);
        document.addAuthor(author);
        document.addCreator(author);
    }

    private void addLineSeparator(Document document) throws DocumentException {
        document.add(new Paragraph(" ", SPACING_FONT));
        document.add(new LineSeparator(0.25f, 100, BaseColor.LIGHT_GRAY, Element.ALIGN_LEFT, 0));
        document.add(new Paragraph(" ", SPACING_FONT));
    }

    private String formatWeight(Weight weight, int significance) {
        String string = "";
        switch (mSettings.getUnits()) {
            case IMPERIAL:
                string = Util.formatImperialWeight(weight, significance);
                break;
            case METRIC:
                string = Util.formatMetricWeight(weight, significance);
                break;
        }
        return string;
    }

    private Image loadDrawable(int resId, float scale)  {
        Drawable d = mContext.getResources().getDrawable(resId);
        BitmapDrawable bitDw = ((BitmapDrawable) d);
        Bitmap bmp = bitDw.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Image image = null;
        try {
            image = Image.getInstance(stream.toByteArray());
            image.scaleAbsolute(scale, scale);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }
}
