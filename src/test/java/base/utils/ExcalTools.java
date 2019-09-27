package base.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;

import base.pojo.ApiCaseDetail;
import base.pojo.WriteDate;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcalTools {
	/**
	 * 读取Excel文件数据
	 * @param url 读取文件路径
	 * @param sheetNum  表单索引
	 * @param clazz 创建一个对象，等待使用反射
	 * @return
	 */
	public static ArrayList<Object> readExcal(String url, int sheetNum, Class clazz) {
		try {
			// 创建一个List 接收person
			ArrayList<Object> pList = new ArrayList<Object>();
			// 2.通过流创建：：类.class.getResourceAsStream()
			InputStream file = ExcalTools.class.getResourceAsStream(url);
			// 1.工作薄，，通过工作薄的工厂创建，需要引用一个文件，通过上步骤的流创建
			Workbook workbook = WorkbookFactory.create(file);
			// 获取指定的表单：传名称，索引等都可以
			Sheet sheetAt = workbook.getSheetAt(sheetNum);

			/** -----获得第一行的数据（属性/变量）--开始----------------------------- */
			Row row = sheetAt.getRow(0);
			// 确定第一行多少列(获得最大列的编号)
			short lastCellNum = row.getLastCellNum();
			// 创建一个容器，存放所有的属性名字（长度就等于最大列，不用加1）
			String[] bialiangs = new String[lastCellNum];
			// 循环遍历获得索引的列（0到最大列-1）
			for (int i = 0; i < lastCellNum; i++) {
				// 获得当前列，需要设置缺省单元格策略 ，空白可作为空白(空字符串)，而不是null(会出空指针异常)
				Cell cellCurrent = row.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
				String stringValue = cellCurrent.getStringCellValue();
				// 把当前的属性名字添加在容器数组里去
				bialiangs[i] = stringValue;
			}
			/** -----获得第一行的数据（属性/变量）--结束----------------------------- */
			/** -----获得除了第一行的数据（属性/变量的值）--开始----------------------------- */
			// 获的最大的行数
			int lastRowNum = sheetAt.getLastRowNum();
			// 循环遍历获得索引的列（0到最大列）
			for (int i = 1; i <= lastRowNum; i++) {
				// 闯将一个对象，来保存数据行的信息
				Object object = clazz.newInstance();
				// 获得当前行
				Row row2 = sheetAt.getRow(i);
				// 当前行的行号rowNo：
				int rowNo = i + 1;
				// 获得set行号方法
				String setRowNo = "setRowNo";
				// 获取该方法对象
				Method setMethod = clazz.getMethod(setRowNo, int.class);
				// 反射调用，设置行号
				setMethod.invoke(object, rowNo);
				// 遍历所有的数据行
				for (int j = 0; j < lastCellNum; j++) {
					Cell cellCurrent = row2.getCell(j, MissingCellPolicy.CREATE_NULL_AS_BLANK);
					// 读取数据前设置单元格类型
					cellCurrent.setCellType(CellType.STRING);
					// 当前列的值
					String stringCellValue = cellCurrent.getStringCellValue();
					// 参数替换：使用ParameUtils工具类，用正则表达式替换全局变量的值
					stringCellValue = ParameUtils.getReplacedParameter(stringCellValue);
					// 获得当前列的属性名字
					String bialiangs_values = bialiangs[j];
					// (反射)
					// 设setXxx方法
					String method = "set" + (bialiangs_values.charAt(0) + "").toUpperCase()
							+ bialiangs_values.substring(1);
					// 获得类字节码对象
					// Class<Person> pClass = Person.class;
					// 获得setXxx方法
					// Method methodSet = pClass.getMethod(method,
					// String.class);
					Method methodSet = clazz.getDeclaredMethod(method, String.class);
					// 反射调用，将当前列的值stringCellValue，设置为person的所有属性的值
					methodSet.invoke(object, stringCellValue);
				}
				pList.add(object);
			}
			/** -----获得除了第一行的数据（属性/变量的值）--结束----------------------------- */
			return pList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 回写数据（问题：回重新覆盖，只保留最后一次数据）
	 * @param sourcePath 资源原路径
	 * @param writePath 写入数据的路径
	 * @param sheetIndex sheet表单（从0开始）
	 * @param writeDate 写入数据
	 */
	public static void writeBack(String sourcePath, String writePath, int sheetIndex, WriteDate writeDate) {
		InputStream inputStream = null;
		Workbook workbook = null;
		OutputStream outputStream = null;
		try {
			inputStream = ExcalTools.class.getResourceAsStream(sourcePath);
			workbook = WorkbookFactory.create(inputStream);
			// 获取指定的表单：传名称，索引等都可以
			Sheet sheetAt = workbook.getSheetAt(sheetIndex);
			// 获取指定的行索引：行号-1,
			Row row = sheetAt.getRow(writeDate.getRowNo() - 1);
			// 获取指定的列索引：列号-1
			Cell cellCurrent = row.getCell(writeDate.getColumnNo() - 1, MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cellCurrent.setCellType(CellType.STRING);
			cellCurrent.setCellValue(writeDate.getData());
			outputStream = new FileOutputStream(new File(writePath));
			workbook.write(outputStream);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				outputStream.close();
				workbook.close();
				inputStream.close();
			} catch (Exception e2) {
			}
		}
	}

	/**
	 * 批量回写数据池中的数据到文件中
	 * @param writePath 写入数据的路径
	 * @param sheetIndex sheet表单（从0开始）
	 * @param sheetIndexSql sql验证数据写入的表单
	 * @param writeDate  写入数据
	 */
	public static void writeBackBatch(String sourcePath, String writePath, int sheetIndex, int sheetIndexSql) {
		InputStream inputStream = null;
		Workbook workbook = null;
		OutputStream outputStream = null;
		try {
			inputStream = ExcalTools.class.getResourceAsStream(sourcePath);
			workbook = WorkbookFactory.create(inputStream);
			// 获取指定的表单：传名称，索引等都可以
			Sheet sheetAt = workbook.getSheetAt(sheetIndex);
			List<WriteDate> writeDatesList = ApiTools.getWriteDatesList();
			for (WriteDate writeDate : writeDatesList) {
				// 获取指定的行索引：行号-1,
			    Row row = sheetAt.getRow(writeDate.getRowNo() - 1);
				// 获取指定的列索引：列号-1
				Cell cellCurrent = row.getCell(writeDate.getColumnNo() - 1, MissingCellPolicy.CREATE_NULL_AS_BLANK);
				cellCurrent.setCellType(CellType.STRING);
				cellCurrent.setCellValue(writeDate.getData());
			}
			// 获取指定的表单：传名称，索引等都可以
			Sheet sheetAtSql = workbook.getSheetAt(sheetIndexSql);
			List<WriteDate> writeDatesListSqlList = ApiTools.getSqlDatesList();
			for (WriteDate writeDate : writeDatesListSqlList) {
				// 获取指定的行索引：行号-1,
				Row row = sheetAtSql.getRow(writeDate.getRowNo() - 1);
				// 获取指定的列索引：列号-1
				Cell cellCurrent = row.getCell(writeDate.getColumnNo() - 1, MissingCellPolicy.CREATE_NULL_AS_BLANK);
				cellCurrent.setCellType(CellType.STRING);
				cellCurrent.setCellValue(writeDate.getData());
			}
			outputStream = new FileOutputStream(new File(writePath));
			workbook.write(outputStream);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				outputStream.close();
				workbook.close();
				inputStream.close();
			} catch (Exception e2) {
			}
		}
	}
	/**
	 * 
	 * @param sourcePath
	 * @param writePath
	 * @param sheetIndex
	 */
	public static void writeBackBatch2(String sourcePath, String writePath, List<Integer> sheetList) {
		InputStream inputStream = null;
		Workbook workbook = null;
		OutputStream outputStream = null;
		try {
			inputStream = ExcalTools.class.getResourceAsStream(sourcePath);
			workbook = WorkbookFactory.create(inputStream);
			for (int sheetIndex : sheetList) {
			    System.out.println("------------------------"+sheetIndex+"---------@@@@@@@@@@@@@@@@@");
			 // 获取指定的表单：传名称，索引等都可以
	            Sheet sheetAt = workbook.getSheetAt(sheetIndex);
	            while (sheetIndex==0) {
	                List<WriteDate> writeDatesList = ApiTools.getWriteDatesList();
	                for (WriteDate writeDate : writeDatesList) {
	                    // 获取指定的行索引：行号-1,
	                    Row row = sheetAt.getRow(writeDate.getRowNo() - 1);
	                    // 获取指定的列索引：列号-1
	                    Cell cellCurrent = row.getCell(writeDate.getColumnNo() - 1, MissingCellPolicy.CREATE_NULL_AS_BLANK);
	                    cellCurrent.setCellType(CellType.STRING);
	                    cellCurrent.setCellValue(writeDate.getData());
	                }
                }
                System.out.println("------------------------"+sheetIndex+"---------@@@@@@@@@@@@@@@@@");
	            while (sheetIndex==2) {
	                List<WriteDate> writeDatesList = ApiTools.getSqlDatesList();
                    for (WriteDate writeDate : writeDatesList) {
                        // 获取指定的行索引：行号-1,
                        Row row = sheetAt.getRow(writeDate.getRowNo() - 1);
                        // 获取指定的列索引：列号-1
                        Cell cellCurrent = row.getCell(writeDate.getColumnNo() - 1, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cellCurrent.setCellType(CellType.STRING);
                        cellCurrent.setCellValue(writeDate.getData());
                    }
                }
	            outputStream = new FileOutputStream(new File(writePath));
	            workbook.write(outputStream);
            }
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				outputStream.close();
				workbook.close();
				inputStream.close();
			} catch (Exception e2) {
			}
		}
	}
	
	public static void writeBackBatch3(String sourcePath, String writePath, int sheetIndex) {
        InputStream inputStream = null;
        Workbook workbook = null;
        OutputStream outputStream = null;
        try {
            inputStream = ExcalTools.class.getResourceAsStream(sourcePath);
            workbook = WorkbookFactory.create(inputStream);
            // 获取指定的表单：传名称，索引等都可以
            Sheet sheetAt = workbook.getSheetAt(sheetIndex);
            List<WriteDate> writeDatesList = ApiTools.getSqlDatesList();
            for (WriteDate writeDate : writeDatesList) {
                // 获取指定的行索引：行号-1,
                Row row = sheetAt.getRow(writeDate.getRowNo() - 1);
                // 获取指定的列索引：列号-1
                Cell cellCurrent = row.getCell(writeDate.getColumnNo() - 1, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                cellCurrent.setCellType(CellType.STRING);
                cellCurrent.setCellValue(writeDate.getData());
            }
            outputStream = new FileOutputStream(new File(writePath));
            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
                workbook.close();
                inputStream.close();
            } catch (Exception e2) {
            }
        }
    }
}
