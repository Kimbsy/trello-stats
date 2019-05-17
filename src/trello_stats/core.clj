(ns trello-stats.core
  (:require [cheshire.core :as json]
            [clojure.pprint :as pp]
            [org.httpkit.client :as http]
            [quil.core :as q]
            [quil.middleware :as m]
            [trello-stats.data :as data]
            [trello-stats.display :as display]))

(def trello-token (System/getenv "TRELLO_STATS_TOKEN"))
(def trello-key (System/getenv "TRELLO_STATS_KEY"))

(defn make-request
  ([path]
   (make-request path {}))
  ([path query-params]
   (-> @(http/get (str "https://api.trello.com/1" path)
                  {:query-params (merge {:key trello-key
                                         :token trello-token}
                                        query-params)})
       :body
       json/parse-string)))

(defn get-lists
  []
  (make-request "/boards/SFlnDTLY/lists" {:fields "name"
                                          :cards "open"
                                          :card_fields "id"}))

(defn -main
  []
  (let [historic-data (vec (clojure.edn/read-string (slurp "/tmp/foo")))]
    (with-open [w (clojure.java.io/writer "/tmp/foo")]
      (binding [*out* w]
        (pr (data/append-daily-data historic-data (get-lists)))))))

(defn setup
  []
  (q/frame-rate 1)
  (q/background 240)
  {:historic-data (vec (clojure.edn/read-string (slurp "/tmp/foo"))) #_(data/get-fake-historic-data)})

(defn update-state
  [state]
  (q/frame-rate 0)
  state)

(q/defsketch quiltest
  :title "Hex testing"
  :size [1200 900]
  :update update-state
  :setup setup
  :draw display/draw-state
  :middleware [m/fun-mode])
