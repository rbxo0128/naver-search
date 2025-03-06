import io.github.cdimascio.dotenv.Dotenv;
import model.dto.NaverAPIResult;
import model.dto.NaverAPIResultItem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import service.searchapi.NaverSearchAPI;
import util.logger.MyLogger;

import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Application {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        MyLogger logger = new MyLogger(Application.class);
        String filename = "%d_%s";
//        logger.setLevel(Level.parse(dotenv.get("LOG_LEVEL")));

        String mode = dotenv.get("MODE");
        switch (mode) {
            case "DEV":
                filename += "_dev";
                break;
        }

        String searchKeyword = dotenv.get("SEARCH_KEYWORD");
        logger.info(searchKeyword);

        NaverSearchAPI searchAPI = new NaverSearchAPI();
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream fileOut = new FileOutputStream(filename.formatted(System.currentTimeMillis(), searchKeyword) + ".xlsx")) {
            List<NaverAPIResultItem> result = searchAPI.searchByKeyword(searchKeyword);
//            logger.info(result.toString());
            Sheet sheet = workbook.createSheet(searchKeyword);
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("날짜");
            headerRow.createCell(1).setCellValue("링크");
            headerRow.createCell(2).setCellValue("제목");
            headerRow.createCell(3).setCellValue("설명");
            int i = 0;
            for (NaverAPIResultItem item : result) {
                Row row = sheet.createRow(++i);
                row.createCell(0).setCellValue(item.pubDate());
                row.createCell(1).setCellValue(item.link());
                row.createCell(2).setCellValue(item.title());
                row.createCell(3).setCellValue(item.description());
            }
            workbook.write(fileOut);

        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }
}
