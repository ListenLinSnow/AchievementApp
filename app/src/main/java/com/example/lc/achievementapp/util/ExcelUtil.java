package com.example.lc.achievementapp.util;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.example.lc.achievementapp.bean.Achievement;
import com.example.lc.achievementapp.bean.AchievementType;
import com.example.lc.achievementapp.bean.ListNotify;
import com.example.lc.achievementapp.common.AchievementStatus;
import com.example.lc.achievementapp.data.LocalData;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelUtil {

    private static WritableFont arial14font = null;

    private static WritableCellFormat arial14format = null;
    private static WritableFont arial10font = null;
    private static WritableCellFormat arial10format = null;
    private static WritableFont arial12font = null;
    private static WritableCellFormat arial12format = null;
    private final static String UTF8_ENCODING = "UTF-8";

    /**
     * 单元格的格式设置 字体大小 颜色 对齐方式、背景颜色等...
     */
    private static void format() {
        try {
            arial14font = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD);
            arial14font.setColour(jxl.format.Colour.LIGHT_BLUE);
            arial14format = new WritableCellFormat(arial14font);
            arial14format.setAlignment(jxl.format.Alignment.CENTRE);
            arial14format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            arial14format.setBackground(jxl.format.Colour.VERY_LIGHT_YELLOW);

            arial10font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            arial10format = new WritableCellFormat(arial10font);
            arial10format.setAlignment(jxl.format.Alignment.CENTRE);
            arial10format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            arial10format.setBackground(Colour.GRAY_25);

            arial12font = new WritableFont(WritableFont.ARIAL, 10);
            arial12format = new WritableCellFormat(arial12font);
            //对齐格式
            arial12format.setAlignment(jxl.format.Alignment.CENTRE);
            //设置边框
            arial12format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化Excel
     *
     * @param fileName 导出excel存放的地址（目录）
     * @param achiTitles
     * @param typeTitles
     */
    public static void initExcel(String fileName, String[] achiTitles, String[] typeTitles) {
        format();
        WritableWorkbook workbook = null;
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            workbook = Workbook.createWorkbook(file);
            /*//设置表格的名字
            WritableSheet sheet = workbook.createSheet("成就", 0);
            //创建标题栏
            sheet.addCell((WritableCell) new Label(0, 0, fileName, arial14format));
            for (int col = 0; col < achiTitles.length; col++ ) {
                sheet.addCell(new Label(col, 0, achiTitles[col], arial10format));
            }
            //设置行高
            sheet.setRowView(0, 340);*/

            WritableSheet sheet2 = workbook.createSheet("成就数据", 0);
            sheet2.addCell((WritableCell) new Label(0, 0, fileName, arial14format));
            for (int col = 0; col < achiTitles.length; col++ ) {
                sheet2.addCell(new Label(col, 0, achiTitles[col], arial10format));
            }
            //设置行高
            sheet2.setRowView(0, 340);

            WritableSheet sheet3 = workbook.createSheet("类型数据", 1);
            sheet3.addCell((WritableCell) new Label(0, 0, fileName, arial14format));
            for (int col = 0; col < typeTitles.length; col++ ){
                sheet3.addCell(new Label(col, 0, typeTitles[col], arial10format));
            }
            sheet3.setRowView(0, 340);

            workbook.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void writeObjListToExcel(List<Achievement> achiList, List<AchievementType> typeList, String fileName, Context c) {
        if (achiList != null && achiList.size() > 0) {
            WritableWorkbook writebook = null;
            InputStream in = null;
            try {
                WorkbookSettings setEncode = new WorkbookSettings();
                setEncode.setEncoding(UTF8_ENCODING);
                in = new FileInputStream(new File(fileName));
                Workbook workbook = Workbook.getWorkbook(in);
                writebook = Workbook.createWorkbook(new File(fileName), workbook);

                List<String> list = new ArrayList<>();
                Achievement achievement = null;
                /*//设置第一个工作簿的内容
                WritableSheet sheet = writebook.getSheet(0);
                for (int j = 0; j < achiList.size(); j++) {
                    achievement = achiList.get(j);
                    list.clear();
                    list.add(String.valueOf(j + 1));
                    list.add(InternalDataConversion.getStatusById(achievement.getStatus()));
                    list.add(TimeUtil.parseTime(achievement.getStartDate()));
                    list.add(TimeUtil.parseTime(achievement.getEndDate()));
                    list.add(LocalData.getTypeById(achievement.getType()).getContent());
                    list.add(achievement.getTitle());
                    list.add(achievement.getRemarks());

                    for (int i = 0; i < list.size(); i++) {
                        sheet.addCell(new Label(i, j+1, list.get(i), arial12format));
                        if (list.get(i).length() <= 4) {
                            //设置列宽
                            sheet.setColumnView(i, list.get(i).length() + 8);
                        } else {
                            //设置列宽
                            sheet.setColumnView(i, list.get(i).length() + 5);
                        }
                    }
                    //设置行高
                    sheet.setRowView(j+1,350);
                }*/
                //设置第二个工作簿的内容
                WritableSheet sheet = writebook.getSheet(0);
                for (int j = 0; j < achiList.size(); j++){
                    achievement = achiList.get(j);
                    list.clear();
                    list.add(String.valueOf(j + 1));
                    list.add(String.valueOf(achievement.getStatus()));
                    list.add(String.valueOf(achievement.getStartDate()));
                    list.add(String.valueOf(achievement.getEndDate()));
                    list.add(String.valueOf(achievement.getType()));
                    list.add(achievement.getTitle());
                    list.add(achievement.getRemarks());

                    for (int i = 0; i < list.size(); i++){
                        sheet.addCell(new Label(i, j + 1, list.get(i), arial12format));
                        if (list.get(i).length() <= 4){
                            //设置列宽
                            sheet.setColumnView(i, list.get(i).length() + 8);
                        } else {
                            //设置列宽
                            sheet.setColumnView(i, list.get(i).length() + 5);
                        }
                    }
                    sheet.setRowView(j + 1, 350);
                }

                WritableSheet sheet2 = writebook.getSheet(1);
                AchievementType achievementType = null;
                for (int j = 0; j < typeList.size(); j++){
                    achievementType = typeList.get(j);
                    list.clear();
                    list.add(String.valueOf(j + 1));
                    list.add(achievementType.getIcon());
                    list.add(achievementType.getContent());
                    list.add(String.valueOf(achievementType.getWeight()));

                    for (int i = 0; i < list.size(); i++){
                        sheet2.addCell(new Label(i, j + 1, list.get(i), arial12format));
                        if (list.get(i).length() <= 4){
                            //设置列宽
                            sheet2.setColumnView(i, list.get(i).length() + 8);
                        } else {
                            //设置列宽
                            sheet2.setColumnView(i, list.get(i).length() + 5);
                        }
                    }
                    sheet2.setRowView(j + 1, 350);
                }

                writebook.write();

                Toast.makeText(c, "导出Excel成功\n文件路径为/AchievementApp/Achievement.xls", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (writebook != null) {
                    try {
                        writebook.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    /**
     * 读取excel数据
     * @param path
     */
    public static void readExcelData(Context context, String path){
        LocalData.initDBHelper(context);

        List<Achievement> achievementList = null;
        List<AchievementType> typeList = null;
        try {
            InputStream inputStream = new FileInputStream(new File(path));
            Workbook workbook = Workbook.getWorkbook(inputStream);
            Sheet sheet = workbook.getSheet(0);
            Cell cell = null;
            //读取成就表格内容并插入数据库当中
            for (int j = 1; j < sheet.getRows(); j++){
                int status = -1;
                long startDate = 0;
                long endDate = 0;
                int type = 0;
                String title = "";
                String remark = "";
                for (int i = 0; i < sheet.getColumns(); i++){
                    cell = sheet.getCell(i ,j);
                    Log.d("JsonList", cell.getContents());
                    switch (i){
                        case 0:
                            break;
                        case 1:
                            if (cell.getContents().equals("3")){
                                status = AchievementStatus.COMPLETED;
                            }else {
                                status = AchievementStatus.ABANDONED;
                            }
                            break;
                        case 2:
                            startDate = Long.valueOf(cell.getContents());
                            break;
                        case 3:
                            endDate = Long.valueOf(cell.getContents());
                            break;
                        case 4:
                            type = Integer.valueOf(cell.getContents());
                            break;
                        case 5:
                            title = cell.getContents();
                            break;
                        case 6:
                            remark = cell.getContents();
                            break;
                    }
                }
                LocalData.insertAchiData(startDate, endDate, title, remark, type, status);
            }
            //读取成就类型表格内容
            sheet = workbook.getSheet(1);
            for (int j = 1; j < sheet.getRows(); j++){
                String icon = "";
                String content = "";
                int weight = 0;
                for (int i = 0; i < sheet.getColumns(); i++){
                    cell = sheet.getCell(i, j);
                    Log.d("JsonList", cell.getContents());
                    switch (i){
                        case 1:
                            icon = cell.getContents();
                            break;
                        case 2:
                            content = cell.getContents();
                            break;
                        case 3:
                            weight = Integer.valueOf(cell.getContents());
                            break;
                    }
                }
                if(!LocalData.isTypeExists(content)) {
                    LocalData.insertTypeData(icon, content, weight);
                }
            }
            EventBus.getDefault().post(new ListNotify(true, true));
            Toast.makeText(context, "导入成功", Toast.LENGTH_SHORT).show();

            inputStream.close();
            workbook.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
