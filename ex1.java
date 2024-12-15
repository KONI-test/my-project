import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JFileChooser;

public class ex1 {

    public static void main(String[] args) {
        System.out.println("正規化：開始");

        // ファイル選択ダイアログの結果
        int result = 0;
        // 現在の日付と時刻
        Date now = new Date();
        // 入力ファイルパス
        String inputFilePath = "";
        // 出力フォルダパス
        String outputFolder = "";
        // ファイル選択ダイアログ生成
        JFileChooser fileChooser = new JFileChooser();
        // 出力を保存するためのリスト
        List<String> normalizedLines = new ArrayList<>();
        // 日付と時間のフォーマット
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

        try {
            // 入力ファイル選択
            fileChooser.setDialogTitle("正規化するファイルを選択してください。");
            result = fileChooser.showOpenDialog(null);

            // 開くボタン押下以外の場合
            if (result != JFileChooser.APPROVE_OPTION) {
                System.out.println("入力ファイル選択をキャンセルしました。");
                return;
            }

            // ファイルを読み込み
            inputFilePath = fileChooser.getSelectedFile().getAbsolutePath();
            List<String> lines = Files.readAllLines(Paths.get(inputFilePath));

            for (String line : lines) {
                // タブ区切りで分割
                String[] row = line.split("\\t");

                // カラムごとの組み合わせを保存
                List<List<String>> processedColumns = new ArrayList<>();

                // コロン区切りで分割し、リストに設定
                for (String column : row) {                 
                    String[] colonSplitColumns = column.split(":");

                    List<String> subColumns = new ArrayList<>();
                    for (String subColumn : colonSplitColumns) {
                        subColumns.add(subColumn);
                    }
                    processedColumns.add(subColumns);
                }

                // カラムの組み合わせを生成
                generateCombinations(processedColumns, new ArrayList<>(), 0, normalizedLines);
            }

            // 結果出力
            fileChooser.setDialogTitle("出力するフォルダを選択してください。");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            result = fileChooser.showOpenDialog(null);

            // 開くボタン押下以外の場合
            if (result != JFileChooser.APPROVE_OPTION) {
                System.out.println("出力フォルダ選択をキャンセルしました。");
                return;
            }
            outputFolder = fileChooser.getSelectedFile().getAbsolutePath();
            outputFolder = outputFolder + File.separator + "output_" + dateFormat.format(now) + ".tsv";
            Files.write(Paths.get(outputFolder), normalizedLines);

        } catch (IOException e) {
            System.err.println("エラーが発生しました: " + e.getMessage());
        }
        System.out.println("正規化：終了");
    }

    // 再帰的に組み合わせを生成
    private static void generateCombinations(List<List<String>> columns, List<String> current, int index,
            List<String> result) {

        // すべてcurrentに格納できたら、その組み合わせを結果リストに追加
        if (index == columns.size()) {
            // currentリストをカンマ区切りの文字列に変換して、resultリストに追加する
            result.add(String.join("\t", current));
            return;
        }

        // 現在のindexの列から、1つずつ値を取り出して次の深さに進む
        for (String value : columns.get(index)) {
            // 現在の列の値をcurrentリストに追加する
            current.add(value);

            // 次の深さでの組み合わせを生成するために再帰的に呼び出す
            generateCombinations(columns, current, index + 1, result);

            // 再帰呼び出しが終わったら、現在の値を削除して次の値に進む
            current.remove(current.size() - 1);
        }
    }
}
