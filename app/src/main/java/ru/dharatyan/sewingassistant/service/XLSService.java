package ru.dharatyan.sewingassistant.service;

import android.content.Context;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import ru.dharatyan.sewingassistant.R;
import ru.dharatyan.sewingassistant.model.entity.Article;
import ru.dharatyan.sewingassistant.model.entity.Model;
import ru.dharatyan.sewingassistant.model.entity.Operation;
import ru.dharatyan.sewingassistant.model.entity.Position;
import ru.dharatyan.sewingassistant.model.grouping.OperationGroupingKey;

public class XLSService {

    private Workbook book;
    private CellStyle cellStyleAutoWrapTopLeftBorderedExceptBottom;
    private CellStyle cellStyleAutoWrapCenterBorderedExceptTop;
    private CellStyle cellStyleCenterBorderedTallTop;
    private CellStyle cellStyleCenterBordered13;
    private CellStyle cellStyleAutoWrapTopLeftBorderedExceptTop;
    private Font fontBold;
    private Font fontUnderlined13;
    private Font font13;
    private final String lastnameLabel;
    private final String monthLabel;
    private final String dateLabel;
    private final String articleLabel;
    private final String modelLabel;
    private final String salaryLabel;
    private final String paidLabel;
    private final String taxLabel;
    private final String payoffLabel;

    private static final int MIN_HORIZONTAL_SIZE = 13;

    public XLSService(Context context) {
        lastnameLabel = context.getResources().getString(R.string.report_label_lastname);
        monthLabel = context.getResources().getString(R.string.report_label_month);
        dateLabel = context.getResources().getString(R.string.report_label_date);
        articleLabel = context.getResources().getString(R.string.report_label_article);
        modelLabel = context.getResources().getString(R.string.report_label_model);
        salaryLabel = context.getResources().getString(R.string.report_label_salary);
        paidLabel = context.getResources().getString(R.string.report_label_paid);
        taxLabel = context.getResources().getString(R.string.report_label_tax);
        payoffLabel = context.getResources().getString(R.string.report_label_payoff);
    }

    public void createReport(FileOutputStream fos, List<Operation> operations, List<Position> positions, List<Article> articles, List<Model> models, String lastname, String month, double paid) throws IOException {

        book = new HSSFWorkbook();

        fontBold = book.createFont();
        fontBold.setBold(true);

        fontUnderlined13 = book.createFont();
        fontUnderlined13.setUnderline((byte) 1);
        fontUnderlined13.setFontHeightInPoints((short) 13);

        font13 = book.createFont();
        font13.setFontHeightInPoints((short) 13);

        cellStyleAutoWrapTopLeftBorderedExceptBottom = book.createCellStyle();
        cellStyleAutoWrapTopLeftBorderedExceptBottom.setWrapText(true);
        cellStyleAutoWrapTopLeftBorderedExceptBottom.setVerticalAlignment(VerticalAlignment.TOP);
        cellStyleAutoWrapTopLeftBorderedExceptBottom.setAlignment(HorizontalAlignment.LEFT);
        cellStyleAutoWrapTopLeftBorderedExceptBottom.setBorderTop(BorderStyle.THIN);
        cellStyleAutoWrapTopLeftBorderedExceptBottom.setBorderLeft(BorderStyle.THIN);
        cellStyleAutoWrapTopLeftBorderedExceptBottom.setBorderRight(BorderStyle.THIN);

        cellStyleAutoWrapTopLeftBorderedExceptTop = book.createCellStyle();
        cellStyleAutoWrapTopLeftBorderedExceptTop.setWrapText(true);
        cellStyleAutoWrapTopLeftBorderedExceptTop.setVerticalAlignment(VerticalAlignment.TOP);
        cellStyleAutoWrapTopLeftBorderedExceptTop.setAlignment(HorizontalAlignment.LEFT);
        cellStyleAutoWrapTopLeftBorderedExceptTop.setBorderBottom(BorderStyle.THIN);
        cellStyleAutoWrapTopLeftBorderedExceptTop.setBorderLeft(BorderStyle.THIN);
        cellStyleAutoWrapTopLeftBorderedExceptTop.setBorderRight(BorderStyle.THIN);

        cellStyleAutoWrapCenterBorderedExceptTop = book.createCellStyle();
        cellStyleAutoWrapCenterBorderedExceptTop.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleAutoWrapCenterBorderedExceptTop.setAlignment(HorizontalAlignment.CENTER);
        cellStyleAutoWrapCenterBorderedExceptTop.setBorderLeft(BorderStyle.THIN);
        cellStyleAutoWrapCenterBorderedExceptTop.setBorderRight(BorderStyle.THIN);
        cellStyleAutoWrapCenterBorderedExceptTop.setBorderBottom(BorderStyle.THIN);
        cellStyleAutoWrapCenterBorderedExceptTop.setWrapText(true);

        cellStyleCenterBorderedTallTop = book.createCellStyle();
        cellStyleCenterBorderedTallTop.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleCenterBorderedTallTop.setAlignment(HorizontalAlignment.CENTER);
        cellStyleCenterBorderedTallTop.setBorderLeft(BorderStyle.THIN);
        cellStyleCenterBorderedTallTop.setBorderRight(BorderStyle.THIN);
        cellStyleCenterBorderedTallTop.setBorderBottom(BorderStyle.THIN);
        cellStyleCenterBorderedTallTop.setBorderTop(BorderStyle.MEDIUM);

        cellStyleCenterBordered13 = book.createCellStyle();
        cellStyleCenterBordered13.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleCenterBordered13.setAlignment(HorizontalAlignment.CENTER);
        cellStyleCenterBordered13.setBorderLeft(BorderStyle.THIN);
        cellStyleCenterBordered13.setBorderRight(BorderStyle.THIN);
        cellStyleCenterBordered13.setBorderTop(BorderStyle.THIN);
        cellStyleCenterBordered13.setBorderBottom(BorderStyle.THIN);
        cellStyleCenterBordered13.setFont(font13);

        Map<Boolean, List<Operation>> operationParts = operations.stream().collect(Collectors.partitioningBy(Operation::isEnabled));
        int i = 1;
        int horizontalSize = positions.size() + 3;
        for (List<Operation> operationsPart : operationParts.values()) {
            Sheet sheet = book.createSheet(String.format(Locale.getDefault(), "Лист %d", i++));

            setTitle(sheet, lastname, month, horizontalSize);
            setHeader(sheet, positions);
            int rowNumber = setBody(sheet, 3, operationsPart, positions, articles, models);
            rowNumber = setTotal(sheet, rowNumber, operationsPart, positions);
            setFooter(sheet, operations, positions, paid, horizontalSize, rowNumber);
            setWidth(sheet, horizontalSize);
        }

        book.write(fos);
        book.close();
    }

    private void setWidth(Sheet sheet, int horizontalSize) {
        if (horizontalSize < MIN_HORIZONTAL_SIZE) horizontalSize = MIN_HORIZONTAL_SIZE;
        sheet.setColumnWidth(0, 2500);
        sheet.setColumnWidth(1, 2200);
        sheet.setColumnWidth(2, 2200);
        for (int i = 3; i < horizontalSize; i++) sheet.setColumnWidth(i, 1500);
    }

    private void setFooter(Sheet sheet, List<Operation> operations, List<Position> positions, double paid, int horizontalSize, int rowNumber) {
//        int step = horizontalSize / 4 - 1;
//        int mod = horizontalSize % 4;
//        int rightBound = 0;
//        int leftBound = rightBound + step + (mod > 0 ? 1 : 0);
//        mod--;

        double salary = 0;
        for (Operation operation : operations)
            salary += operation.getQuantity() * positions.stream().filter(position -> position.getId().equals(operation.getPositionId())).findAny().get().getCost();
        Row footerRow = sheet.createRow(rowNumber);
        footerRow.setHeight((short) 500);

        Cell salaryCell = footerRow.createCell(0);
        salaryCell.setCellStyle(cellStyleCenterBordered13);
        footerRow.createCell(1).setCellStyle(cellStyleCenterBordered13);
        salaryCell.setCellValue(String.format(Locale.getDefault(), "%s %.2f", salaryLabel, salary));
//        if (leftBound != rightBound)
        sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 0, 1));

//        rightBound = leftBound + 1;
//        leftBound = rightBound + step + (mod > 0 ? 1 : 0);
//        mod--;

        Cell paidCell = footerRow.createCell(2);
        footerRow.createCell(3).setCellStyle(cellStyleCenterBordered13);
        footerRow.createCell(4).setCellStyle(cellStyleCenterBordered13);
        paidCell.setCellStyle(cellStyleCenterBordered13);
        paidCell.setCellValue(String.format(Locale.getDefault(), "%s %.2f", paidLabel, paid));
//        if (leftBound != rightBound)
        sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 2, 4));
//        rightBound = leftBound + 1;
//        leftBound = rightBound + step + (mod > 0 ? 1 : 0);
//        mod--;

        Cell taxCell = footerRow.createCell(5);
        footerRow.createCell(5).setCellStyle(cellStyleCenterBordered13);
        footerRow.createCell(6).setCellStyle(cellStyleCenterBordered13);
        footerRow.createCell(7).setCellStyle(cellStyleCenterBordered13);
        footerRow.createCell(8).setCellStyle(cellStyleCenterBordered13);
        taxCell.setCellStyle(cellStyleCenterBordered13);
        taxCell.setCellValue(String.format(Locale.getDefault(), "%s       ", taxLabel));
//        if (leftBound != rightBound)
        sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 5, 8));
//        rightBound = leftBound + 1;
//        leftBound  = rightBound + step + (mod > 0 ? 1 : 0);

        Cell payoffCell = footerRow.createCell(9);
        footerRow.createCell(10).setCellStyle(cellStyleCenterBordered13);
        footerRow.createCell(11).setCellStyle(cellStyleCenterBordered13);
        footerRow.createCell(12).setCellStyle(cellStyleCenterBordered13);
        payoffCell.setCellStyle(cellStyleCenterBordered13);
        payoffCell.setCellValue(String.format(Locale.getDefault(), "%s       ", payoffLabel));
//        if (leftBound != rightBound)
        sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 9, 12));
    }

    private int setTotal(Sheet sheet, int rowNumber, List<Operation> operations, List<Position> positions) {
        Row totalCountByPositionRow  = sheet.createRow(rowNumber);
        Row totalSumByPositionRow = sheet.createRow(rowNumber + 1);
        Row totalSumRow = sheet.createRow(rowNumber + 2);

        for (int i = 0; i < 3; i++) {
            totalCountByPositionRow.createCell(i).setCellStyle(cellStyleCenterBorderedTallTop);
            totalSumByPositionRow.createCell(i).setCellStyle(cellStyleAutoWrapCenterBorderedExceptTop);
            totalSumRow.createCell(i).setCellStyle(cellStyleCenterBordered13);
        }
        sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 0, 2));
        sheet.addMergedRegion(new CellRangeAddress(rowNumber + 1, rowNumber + 1, 0, 2));
        sheet.addMergedRegion(new CellRangeAddress(rowNumber + 2, rowNumber + 2, 0, 2));

        int colNumber = 3;
        double totalSum = 0;
        for (Position position : positions) {
            List<Operation> operationsByPosition = operations.stream().filter(operation -> operation.getPositionId().equals(position.getId())).collect(Collectors.toList());
            Cell countCell = totalCountByPositionRow.createCell(colNumber);
            countCell.setCellStyle(cellStyleCenterBorderedTallTop);
            Cell sumCell = totalSumByPositionRow.createCell(colNumber);
            sumCell.setCellStyle(cellStyleAutoWrapCenterBorderedExceptTop);
            totalSumRow.createCell(colNumber).setCellStyle(cellStyleCenterBordered13);
            if (operationsByPosition.size() > 0) {
                int count = 0;
                for (Operation operation : operationsByPosition) count += operation.getQuantity();
                countCell.setCellValue(count);
                double sum = count * position.getCost();
                sumCell.setCellValue(sum);
                totalSum += sum;
            }
            colNumber++;
        }
        Cell totalSumCell = totalSumRow.getCell(3);
        if (totalSumCell != null) {
            totalSumCell.setCellValue(totalSum);
            if (positions.size() > 1) sheet.addMergedRegion(new CellRangeAddress(rowNumber + 2, rowNumber + 2, 3, positions.size() + 2));
        }
        return rowNumber + 3;
    }

    private int setBody(Sheet sheet, int rowNumber, List<Operation> operations, List<Position> positions, List<Article> articles, List<Model> models) {
        Map<OperationGroupingKey, List<Operation>> operationsGrouped = operations.stream().collect(
                Collectors.groupingBy(
                        operation -> {
                            Article article = articles.stream().filter(a -> a.getId().equals(operation.getArticleId())).findAny().get();
                            Model model = models.stream().filter(m -> m.getId().equals(article.getModelId())).findAny().get();
                            return new OperationGroupingKey(operation.getDate(), article, model);
                        }));

        for (OperationGroupingKey operationGroupingKey : operationsGrouped.keySet().stream().sorted(Comparator
                .comparing((OperationGroupingKey ogk) -> ogk.getDate().toString())
                .thenComparing((OperationGroupingKey ogk) -> ogk.getModel().toString())
                .thenComparing((OperationGroupingKey ogk) -> ogk.getArticle().toString())).collect(Collectors.toList())) {
            Row row = sheet.createRow(rowNumber++);
            int colNumber = 0;
            Cell cellDate = row.createCell(colNumber++);
            cellDate.setCellValue(operationGroupingKey.getDate().toString());
            cellDate.setCellStyle(cellStyleAutoWrapTopLeftBorderedExceptTop);
            Cell cellArticle = row.createCell(colNumber++);
            cellArticle.setCellValue(operationGroupingKey.getArticle().getName());
            cellArticle.setCellStyle(cellStyleAutoWrapTopLeftBorderedExceptTop);
            Cell cellModel = row.createCell(colNumber++);
            cellModel.setCellValue(operationGroupingKey.getModel().getName());
            cellModel.setCellStyle(cellStyleAutoWrapTopLeftBorderedExceptTop);

            List<Operation> operationGroup = operationsGrouped.get(operationGroupingKey);
            for (Position position : positions) {
                Cell cellQuantity = row.createCell(colNumber++);
                cellQuantity.setCellStyle(cellStyleAutoWrapCenterBorderedExceptTop);
                operationGroup.stream().filter(operation -> operation.getPositionId().equals(position.getId())).findAny().ifPresent(operation ->
                        cellQuantity.setCellValue(operation.getQuantity()));
            }
        }

        return rowNumber;
    }

    private void setHeader(Sheet sheet, List<Position> positions) {
        int i = 0;
        Row upperRow = sheet.createRow(1);
        Row lowerRow = sheet.createRow(2);

        Cell cellDate = upperRow.createCell(i);
        cellDate.setCellValue(dateLabel);
        cellDate.setCellStyle(cellStyleAutoWrapTopLeftBorderedExceptBottom);
        lowerRow.createCell(i++).setCellStyle(cellStyleAutoWrapCenterBorderedExceptTop);

        Cell cellArticle = upperRow.createCell(i);
        cellArticle.setCellValue(articleLabel);
        cellArticle.setCellStyle(cellStyleAutoWrapTopLeftBorderedExceptBottom);
        lowerRow.createCell(i++).setCellStyle(cellStyleAutoWrapCenterBorderedExceptTop);

        Cell cellModel = upperRow.createCell(i);
        cellModel.setCellValue(modelLabel);
        cellModel.setCellStyle(cellStyleAutoWrapTopLeftBorderedExceptBottom);
        lowerRow.createCell(i++).setCellStyle(cellStyleAutoWrapCenterBorderedExceptTop);

        for (Position position : positions) {
            Cell cellPositionName = upperRow.createCell(i);
            cellPositionName.setCellValue(position.getName());
            cellPositionName.setCellStyle(cellStyleAutoWrapTopLeftBorderedExceptBottom);

            Cell cellPositionCost = lowerRow.createCell(i++);
            RichTextString costText = new HSSFRichTextString(String.valueOf(position.getCost()));
            costText.applyFont(0, costText.length(), fontBold);
            cellPositionCost.setCellValue(costText);
            cellPositionCost.setCellStyle(cellStyleAutoWrapCenterBorderedExceptTop);
        }

    }

    private void setTitle(Sheet sheet, String lastname, String month, int horizontalSize) {
        Row titleRow = sheet.createRow(0);
        titleRow.setHeight((short) 500);
        Cell cellTitle = titleRow.createCell(0);

        CellStyle cellStyleTopCenter = book.createCellStyle();
        cellStyleTopCenter.setAlignment(HorizontalAlignment.CENTER);
        cellStyleTopCenter.setVerticalAlignment(VerticalAlignment.TOP);

        if (lastname.length() == 0) lastname = "\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020" +
                "\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020";
        if (month.length() == 0) month = "\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020" +
                "\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020";
        cellTitle.setCellValue(prepareTitle(lastname, month, "\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020" +
                "\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020"));
        cellTitle.setCellStyle(cellStyleTopCenter);

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, Math.max(horizontalSize, MIN_HORIZONTAL_SIZE) - 1));
    }

    private HSSFRichTextString prepareTitle(String lastname, String month, String padding) {
        lastname = "  " + lastname + "  ";
        month = "  " + month + "  ";
        int startLastnamePos = lastnameLabel.length();
        int endLastnamePos = startLastnamePos + lastname.length();
        int startMonthPos = endLastnamePos + padding.length() + monthLabel.length();
        int endMonthPos = startMonthPos + month.length();

        HSSFRichTextString richTextString = new HSSFRichTextString(lastnameLabel + lastname + padding + monthLabel + month + "_");
        richTextString.applyFont(0, endMonthPos, font13);
        richTextString.applyFont(startLastnamePos, endLastnamePos, fontUnderlined13);
        richTextString.applyFont(startMonthPos, endMonthPos, fontUnderlined13);

        return richTextString;
    }
}
