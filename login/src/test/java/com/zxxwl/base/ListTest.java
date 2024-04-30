package com.zxxwl.base;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ListTest {
    public static void main(String[] args) {
        List<String> names = List.of("Alice", "Bob", "Charlie", "Alex", "Ben", "Andy");

        // 使用拼音首字母进行分组
        Map<Character, List<String>> groupedNames = names.stream().collect(Collectors.groupingBy(name -> name.charAt(0)));

        log.info("{}", groupedNames);
        // 输出分组结果
        groupedNames.forEach((initial, group) -> {
            System.out.println("Names starting with " + initial + ": " + group);
        });
    }
}
