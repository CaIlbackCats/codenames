package com.callbackcats.codenames.util;

import com.callbackcats.codenames.game.card.domain.GameLanguage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class WordImporter {


/*    INSERT INTO mars.provider_account (id, name, provider_service_name, password, email, phone, role, zipcode, city,
    address, age_group_min, age_group_max)
    VALUES (1, 'test', 'tesztelek', '$2y$10$HssVEkgvwfRehsw7Dl9nfek5PNwdWfiSkF7A2TaNiAeCRFH8691/m', 'pecske92@gmail.com',
                    '1234 567', 'ROLE_PROVIDER', '9500', 'pest', 'asd', 0, 79);*/


    public static void main(String[] args) {

        File hunFile = new File("src/main/resources/util/szavak.txt");
        File engFile = new File("src/main/resources/util/cnwordlist.txt");

        File sqlScript = new File("src/main/resources/util/wordImporter.sql");

        try {
            BufferedWriter wr = new BufferedWriter(new FileWriter(sqlScript));
            Map<File, GameLanguage> languageMap = new HashMap<>();
            languageMap.put(hunFile, GameLanguage.HUNGARIAN);
            languageMap.put(engFile, GameLanguage.ENGLISH);

            languageMap.entrySet().stream().forEach(entry -> {
                try {
                    generateSQLScript(entry, wr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void generateSQLScript(Map.Entry<File, GameLanguage> entry, BufferedWriter wr) throws IOException {
        Scanner sc = new Scanner(entry.getKey());
        String currentLanguage = entry.getValue().name();
        while (sc.hasNext()) {
            String currentLine = "INSERT INTO codenames.word (word, language) \n";
            String word = sc.nextLine();
            currentLine += "VALUES ( '" + word + "', '" + currentLanguage + "');";
            wr.write(currentLine);
            wr.newLine();
            wr.flush();
        }

    }
}
