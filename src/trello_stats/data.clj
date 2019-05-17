(ns trello-stats.data
  (:require [clojure.pprint :as pp]))

(defn get-fake-historic-data
  []
  [{:date 0, :sprint-backlog 9, :in-progress 5, :pr 2, :done 0}
   {:date 1, :sprint-backlog 10, :in-progress 6, :pr 2, :done 1}
   {:date 2, :sprint-backlog 11, :in-progress 5, :pr 3, :done 2}
   {:date 3, :sprint-backlog 10, :in-progress 5, :pr 4, :done 3}
   {:date 4, :sprint-backlog 9, :in-progress 6, :pr 2, :done 5}
   {:date 5, :sprint-backlog 9, :in-progress 5, :pr 1, :done 7}])

(defn append-daily-data
  [historic-data daily-lists]
  (conj historic-data (-> (into {}
                                (map (fn [list] [(-> (get list "name")
                                                     clojure.string/lower-case
                                                     (clojure.string/replace #" " "-")
                                                     keyword)
                                                 (count (get list "cards"))])
                                     daily-lists))
                          (assoc :date (->> (map :date historic-data)
                                            (cons 0)
                                            (reduce max)
                                            inc)))))
