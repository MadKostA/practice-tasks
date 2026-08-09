package org.example.practice_tasks_1;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CollectionsTasksTest {

    @ParameterizedTest
    @MethodSource
    void mergeSortedTests(List<Integer> a, List<Integer> b, List<Integer> expectedResult) {
        List<Integer> actualResult = CollectionsTasks.mergeSorted(a, b);

        assertThat(actualResult)
                .isEqualTo(expectedResult);
    }

    private static Stream<Arguments> mergeSortedTests() {
        return Stream.of(
                Arguments.of(new ArrayList<>(Arrays.asList(1, 2, 3)),
                        new ArrayList<>(Arrays.asList(1, 2, 3)),
                        List.of(1, 1, 2, 2, 3, 3)),
                Arguments.of(new ArrayList<>(Arrays.asList(1, 2, 3)),
                        new ArrayList<>(Arrays.asList(4, 5, 6)),
                        List.of(1, 2, 3, 4, 5, 6)),
                Arguments.of(new ArrayList<>(Arrays.asList(1, 3, 5)),
                        new ArrayList<>(Arrays.asList(2, 4, 6)),
                        List.of(1, 2, 3, 4, 5, 6)),
                Arguments.of(new ArrayList<>(), new ArrayList<>(), List.of()),
                Arguments.of(null, null, List.of()),
                Arguments.of(new ArrayList<>(Arrays.asList(1, 2, 3)),
                        null, List.of(1, 2, 3)),
                Arguments.of(null,
                        new ArrayList<>(Arrays.asList(1, 2, 3)),
                        List.of(1, 2, 3)),
                Arguments.of(new ArrayList<>(Arrays.asList(1, 2, 3)),
                        null, List.of(1, 2, 3))
        );
    }

    @ParameterizedTest
    @MethodSource
    void wordFrequencyTests(String request, Map<String, Integer> expectedResult) {
        Map<String, Integer> actualResult = CollectionsTasks.wordFrequency(request);

        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    private static Stream<Arguments> wordFrequencyTests() {
        return Stream.of(
                Arguments.of("a  a a b b", Map.of("a", 3, "b", 2)),
                Arguments.of("a   a a b b", Map.of("a", 3, "b", 2)),
                Arguments.of("  a a a b b  ", Map.of("a", 3, "b", 2)),
                Arguments.of("a,  a a b b", Map.of("a,", 1, "a", 2, "b", 2)),
                Arguments.of("  ", Map.of()),
                Arguments.of(null, Map.of())
        );
    }

}