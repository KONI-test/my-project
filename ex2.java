import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import javax.swing.JFileChooser;

public class ex2 {

    public static void main(String[] args) throws IOException {
        // ダイアログの結果を保持
        int result = 0;
        // 入力ファイルパス
        String inputFilePath = "";
        // ファイル選択ダイアログの表示
        JFileChooser fileChooser = new JFileChooser();

        // 入力ファイル選択
        fileChooser.setDialogTitle("正規化するファイルを選択してください。");
        result = fileChooser.showOpenDialog(null);

        // 開くボタン押下以外の場合
        if (result != JFileChooser.APPROVE_OPTION) {
            System.out.println("入力ファイル選択をキャンセルしました。");
            return; // 処理を終了
        }

        // 選択されたファイルのパスを取得
        inputFilePath = fileChooser.getSelectedFile().getAbsolutePath();

        // ファイルを1行ずつ読み込み
        List<String> lines = Files.readAllLines(Paths.get(inputFilePath));

        // 読み込んだデータをString[]のリストに変換
        List<String[]> arrayList = new ArrayList<>();
        for (String str : lines) {
            // タブ文字（\t）で分割して配列化
            String[] list = str.split("\t");

            // 行末にタブがあった場合、空文字を追加
            if (str.charAt(str.length() - 1) == '\t') {
                String[] newList = Arrays.copyOf(list, list.length + 1);
                newList[newList.length - 1] = ""; // 最後に空文字を追加
                list = newList;
            }

            // 配列をリストに追加
            arrayList.add(list);
        }

        // 結果出力
        System.out.println(normalizeCsvToTsv(arrayList));
    }

    public static String normalizeCsvToTsv(List<String[]> inputCsv) {

        // 正規化されたTSVデータ
        StringBuilder normalizedData = new StringBuilder();
        // カテゴリごと格納するマップ
        Map<String, List<String>> categoryToValues = new LinkedHashMap<>();

        // 入力データが空か、列数が2でない場合はエラー
        if (inputCsv.isEmpty() || inputCsv.get(0).length != 2) {
            throw new IllegalArgumentException("入力データは2列でなければなりません。");
        }

        // 入力データをループしてカテゴリと値をマップに格納
        for (String[] row : inputCsv) {
            String category = row[0];
            String value = row[1];

            // カテゴリがまだマップに登録されていなければ、新しいリストを作成
            categoryToValues.computeIfAbsent(category, key -> new ArrayList<>());

            // 現在のカテゴリに値を追加
            categoryToValues.get(category).add(value);
        }

        // マップの内容をループ
        for (Map.Entry<String, List<String>> entry : categoryToValues.entrySet()) {
            String category = entry.getKey();
            List<String> values = entry.getValue();

            // 値の重複を削除し、「:」で結合
            String joinedValues = values.stream()
                    .distinct() // 重複を削除
                    .reduce((a, b) -> a + ":" + b) // 「:」で結合
                    .orElse(""); // 値がない場合は空文字を返す

            // 結果をTSV形式で追加
            normalizedData.append(category).append("\t").append(joinedValues).append("\n");
        }

        // 最終的なTSVデータを返す
        return normalizedData.toString().trim();
    }

}
