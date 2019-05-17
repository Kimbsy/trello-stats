(ns trello-stats.display
  (:require [quil.core :as q]))

(def yellow [226 244 66])
(def light-blue [91 146 234])
(def dark-blue [34 87 173])
(def green [66 244 119])

(defn draw-shape
  [[top-fn previous-top-fns color] historic-data]
  (q/fill color)
  (q/begin-shape)
  (doall (map (fn [d]
                (let [y (- (+ (top-fn d)
                              (reduce + (map #(% d) previous-top-fns))))]
                  (q/vertex (* 150 (:date d)) (* 20 y))))
              historic-data))
  (doall (map (fn [d]
                (let [y (- (reduce + (map #(% d) previous-top-fns)))]
                  (q/vertex (* 150 (:date d)) (* 20 y))))
              (reverse historic-data)))
  (q/end-shape :close))

(def layers [[:done [(constantly 0)] green]
             [:pr [:done (constantly 0)]  dark-blue]
             [:in-progress  [:pr :done (constantly 0)] light-blue]
             [:sprint-backlog [:in-progress :pr :done (constantly 0)] yellow]])

(defn draw-state
  [{:keys [historic-data] :as state}]
  (q/background 240)
  (q/with-translation [100 800]
    (doall (map #(draw-shape % historic-data) layers))))
