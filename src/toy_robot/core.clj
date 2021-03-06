(ns toy-robot.core
  (:require [clojure.string :as s]
            [clojure.java.io :as io])
  (:use clojure.pprint))

(def table
  "Max x,y limits of the table top, with origin 0,0"
  [5 5])

(defn would-fall? [{:keys [x y]}]
  "Would the robot fall from the table, given proposed coordinates"
  (let [max-x (first table)
        max-y (second table)]
    (not
      (and (<= x max-x)
           (>= x 0)
           (<= y max-y)
           (>= y 0)))))

(defn move [robot]
  (let [new-pos (condp = (:direction robot)
                  :north (update-in robot [:y] inc)
                  :east  (update-in robot [:x] inc)
                  :south (update-in robot [:y] dec)
                  :west  (update-in robot [:x] dec))]
    (if (would-fall? new-pos)
      robot
      new-pos)))

(defn pos-summary [{:keys [x y direction]}]
  (s/join ", " [x y (s/upper-case (name direction))]))

(defn rotate
  "Given an existing direction (:north, :south etc.), calculate the
   next direction by applying a rotation function (- or +)"
  [rotate-fn curr-dir]
  (let [directions [:north :east :south :west]
        index      (.indexOf directions curr-dir)
        next-index (rotate-fn index 1)]
    (cond
      (>= next-index (count directions)) (first directions)
      (neg? next-index) (last directions)
      :else (nth directions next-index))))

(defn turn-robot [robot rotation]
  (assoc robot
         :direction
         (rotate rotation (:direction robot))))

(defn roam
  "Returns a new robot map consisting of a robot position & direction"
  [robot cmd]
  (condp = (:cmd cmd)
    :place  (assoc robot
                   :x (:x cmd)
                   :y (:y cmd)
                   :direction (:direction cmd))
    :left   (turn-robot robot -)
    :right  (turn-robot robot +)
    :move   (move robot)
    :report (do
              (println (pos-summary robot))
              robot)))

(defn keywordify [s]
  (keyword (s/lower-case s)))

(defn exec [inputs]
  "Takes an input of robot commands and executes them.
  All commands before a :place command will be ignored"
  (let [cmds (drop-while
               #(not= (:cmd %) :place) inputs)]
    (reduce roam {} cmds)))

(defn place-match [line]
  (->> line
       (re-find #"^(PLACE)\s(\d),(\d),(NORTH|SOUTH|EAST|WEST)$")
       (drop 1)))

(defn cmd-match [line]
  (->> line
       (re-find #"^(MOVE|LEFT|RIGHT|REPORT)$")
       (drop 1)
       (first)))

(defn parse-line [line]
  (let [place (place-match line)
        cmd (cmd-match line)]
    (cond
      (some? cmd) {:cmd (keywordify cmd)}
      (some? place) {:cmd :place
                     :x (Integer/parseInt (nth place 1))
                     :y (Integer/parseInt (nth place 2))
                     :direction (keywordify (last place))}
      :else (throw (Exception. "Illegal line format")))))

(def data-files
  (->> (file-seq (io/file "./data"))
       (filter #(.isFile %))))

(defn parse-file [file]
  (with-open [rdr (io/reader file)]
    (let [lines (line-seq rdr)]
      (doall
        (map parse-line lines)))))

(defn -main []
  (dorun
    (map (comp exec parse-file)
         data-files)))