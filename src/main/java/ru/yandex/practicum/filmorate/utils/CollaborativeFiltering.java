package ru.yandex.practicum.filmorate.utils;

import java.util.HashMap;
import java.util.Map;

public class CollaborativeFiltering {

    private static final Map<Long, Map<Long, Double>> diff = new HashMap<>(); // <filmId, <filmId, diff>> для расчета различий между оценками пользователей
    private static final Map<Long, Map<Long, Integer>> freq = new HashMap<>(); // <filmId, <filmId, freq>> количество вхождений эл-тов

    public static HashMap<Long, Double> predictRatings(Map<Long, HashMap<Long, Integer>> inputData, Long userId) {
        buildDifferencesMatrix(inputData);
        return predict(inputData, userId);
    }

    /* расчет отношений между элементами и количества вхождений элементов.*/
    private static void buildDifferencesMatrix(Map<Long, HashMap<Long, Integer>> data) {
        //Для каждого пользователя проверяем его рейтинг фильмов:
        for (HashMap<Long, Integer> user : data.values()) {
            for (Map.Entry<Long, Integer> e : user.entrySet()) {
                //существует ли элемент в матрицах
                if (!diff.containsKey(e.getKey())) {
                    diff.put(e.getKey(), new HashMap<>());
                    freq.put(e.getKey(), new HashMap<>());
                }
                // сравнить рейтинги всех элементов и посчитать вхождение:
                for (Map.Entry<Long, Integer> e2 : user.entrySet()) {
                    int oldCount = freq.get(e.getKey()).getOrDefault(e2.getKey(), 0);
                    double oldDiff = diff.get(e.getKey()).getOrDefault(e2.getKey(), 0.0);
                    int observedDiff = e.getValue() - e2.getValue();
                    freq.get(e.getKey()).put(e2.getKey(), oldCount + 1);
                    diff.get(e.getKey()).put(e2.getKey(), oldDiff + observedDiff);
                }
            }
        }
        //вычисляем оценки сходства внутри матриц: разница рассчитанного рейтинга элемента / количество его вхождений:
        for (Long i : diff.keySet()) {
            for (Long j : diff.get(i).keySet()) {
                double oldValue = diff.get(i).get(j);
                int count = freq.get(i).get(j);
                diff.get(i).put(j, oldValue / count);
            }
        }
    }

    /* Прогноз недостающих рейтингов на основе существующих данных. */
    private static HashMap<Long, Double> predict(Map<Long, HashMap<Long, Integer>> data, long userId) {
        HashMap<Long, Double> uPred = new HashMap<>();
        HashMap<Long, Integer> uFreq = new HashMap<>();
        for (Long filmId : diff.keySet()) {
            uPred.put(filmId, 0.0);
            uFreq.put(filmId, 0);
        }
        //Сравнить рейтинги пользовательских элементов с матрицей различий:
        for (Long filmId1 : data.get(userId).keySet()) {
            for (Long filmId2 : diff.keySet()) {
                try {
                    double predictedValue = diff.get(filmId2).get(filmId1) + data.get(userId).get(filmId1);
                    double finalValue = predictedValue * freq.get(filmId2).get(filmId1);
                    uPred.put(filmId2, uPred.get(filmId2) + finalValue);
                    uFreq.put(filmId2, uFreq.get(filmId2) + freq.get(filmId2).get(filmId1));
                } catch (NullPointerException ignored) {
                }
            }
        }
        //«чистые» прогнозы:
        HashMap<Long, Double> clean = new HashMap<>();
        for (Long filmId : uPred.keySet()) {
            if (uFreq.get(filmId) > 0) {
                clean.put(filmId, (uPred.get(filmId) / uFreq.get(filmId)));
            }
        }
        return clean;
    }
}