(ns vigneron-scraping.core
  (:gen-class)
  (:require [net.cgrand.enlive-html :as html]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))


(slurp "https://www.vigneron-independant.com/chateau-des-correges")

; using enlive

(def url "https://www.vigneron-independant.com/chateau-des-correges")

(def content
  (html/html-resource (java.net.URL. url)))

; stickit-Contact = :body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 7)]


(defn domain-name [content]
  (first (:content
      (first 
        (html/select content [:body :section :div :div.panel-display.panel-1col.clearfix :div :div :div :h2])))))

(defn name-1 [content]
  (first (:content 
    (first 
      (html/select content [:body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 7)] :div.establishment-contact :div :div.fn.org :div :div :div.field-item.even])))))

(defn name-2 [content]
  (first (:content 
      (second 
        (html/select content [:body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 7)] [:div (html/nth-child 2)]])))))

(defn street-address [content]
  (second 
    (:content 
      (second 
        (html/select content [:body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 7)] [:div (html/nth-child 2)] :div])))))

(defn postal-code [content]
  (first (:content
      (first 
        (html/select content [:body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 7)] :div.establishment-contact :div :div.adr :span.postal-code])))))

(defn locality [content]
  (first (:content
      (first 
        (html/select content [:body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 7)] :div.establishment-contact :div :div.adr :span.locality])))))

(defn mobile [content]
  (first (:content
      (first 
        (html/select content [:body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 7)] :div.establishment-contact :div :div.tel :span])))))

(defn website [content]
  (first (:content
      (first (html/select content [:body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 7)] :div.establishment-contact :div :div.url :a])))))




(defn wine-name [content] 
  (first (:content (first 
      (html/select content [:body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 8)] [:div (html/nth-child 1)] :div :div :div])))))

(defn wine-place [content] 
  (first (:content (second 
      (html/select content [:body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 8)] [:div (html/nth-child 1)] :div :div :div])))))

(defn wine-designation [content]
  (first (:content (first 
      (html/select content [:body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 8)] [:div (html/nth-child 1)] :div :div :div [:li (html/nth-child 1)]])))))

(defn wine-domain [content]
  (first (:content (first 
      (html/select content [:body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 8)] [:div (html/nth-child 1)] :div :div :div [:li (html/nth-child 2)]])))))

(defn wine-type [content]
  (first (:content (first 
      (html/select content [:body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 8)] [:div (html/nth-child 1)] :div :div :div [:li (html/nth-child 3)]])))))

(defn wine-color [content]
  (first (:content (first 
      (html/select content [:body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 8)] [:div (html/nth-child 1)] :div :div :div [:li (html/nth-child 3)]])))))




(defn page-data [url]
  (let [content (html/html-resource (java.net.URL. url))]
    {:domain-name (domain-name content)
     :name-1 (name-1 content)
     :name-2 (name-2 content)
     :street-address (street-address content)
     :postal-code (postal-code content)
     :locality (locality content)
     :mobile (mobile content)
     :website (website content)
     :wine {:wine-name (wine-name content)
            :wine-place (wine-place content)
            :wine-designation (wine-designation content)
            :wine-domain (wine-domain content)
            :wine-type (wine-type content)
            :wine-color (wine-color content)}}))
(page-data url)


(defn type-is-str [thing]
  (if (= (type thing) (type ""))
    true
    false))

(defn nil-to-empty-str [thing]
  (if thing 
    ; if type is not string : empty str
    (if (type-is-str thing) 
      thing
      "")
    ""))

(defn excel-row [page-data]
  (vec 
    (map nil-to-empty-str
      [(page-data :domain-name)
       (page-data :name-1)
       (page-data :name-2)
       (page-data :street-address)
       (page-data :postal-code)
       (page-data :locality)
       (page-data :mobile)
       (page-data :website)
       (get-in page-data [:wine :wine-name])
       (get-in page-data [:wine :wine-place])
       (get-in page-data [:wine :wine-designation])
       (get-in page-data [:wine :wine-domain])
       (get-in page-data [:wine :wine-type])
       (get-in page-data [:wine :wine-color])
       ])))

(excel-row (page-data url))

(def url-list
  [ ; page 1
   "https://www.vigneron-independant.com/champagne-lejeune-pere-et-fils"
   "https://www.vigneron-independant.com/domaine-du-val-de-gilly"
   "https://www.vigneron-independant.com/chateau-des-correges"
   "https://www.vigneron-independant.com/vignobles-tourrel"
   "https://www.vigneron-independant.com/domaine-cartaux-bougaud"
   "https://www.vigneron-independant.com/domaine-dugois"
   "https://www.vigneron-independant.com/domaine-delaunay-0"
   "https://www.vigneron-independant.com/musculus-claude"
   "https://www.vigneron-independant.com/domaine-du-pic"
   ; page 2
   "https://www.vigneron-independant.com/domaine-thomas-labaille"
   "https://www.vigneron-independant.com/chateau-les-hauts-daglan"
   "https://www.vigneron-independant.com/chateau-la-bretonniere"
   "https://www.vigneron-independant.com/champagne-jean-yves-de-carlini"
   "https://www.vigneron-independant.com/domaine-turenne"
   "https://www.vigneron-independant.com/noulens-christian"
   "https://www.vigneron-independant.com/chateau-sainte-roseline"
   "https://www.vigneron-independant.com/domaine-de-valette"
   "https://www.vigneron-independant.com/domaine-du-moura"
   "https://www.vigneron-independant.com/chateau-les-crostes"
   "https://www.vigneron-independant.com/chateau-la-martinette"
  ])

(count url-list)

; scrapes the pages
(def data
  (vec
    (map page-data url-list)))


(def title-list 
  ["domain-name" "name-1" "name-2" "street-address" "postal-code" "locality" "mobile" "website" "wine-name" "wine-place" "wine-designation" "wine-domain" "wine-type" "wine-color"])

(defn for-excel []
  `[~title-list ~@(vec (map excel-row data))])

(count for-excel)

; to excel
(use 'dk.ative.docjure.spreadsheet)

;; Create a spreadsheet and save it
(let [wb (create-workbook "vigneron data"
                          ; [["domain-name" "name-1" "name-2" "street-address" "postal-code" "locality" "mobile" "website" "wine-name" "wine-place" "wine-designation" "wine-domain" "wine-type" "wine-color"]
                          ;  (excel-row (data 0))
                          ;  (excel-row (data 1))
                          ;  ]
                          (for-excel)
                          )
      sheet (select-sheet "vigneron data" wb)
      header-row (first (row-seq sheet))]
  (set-row-style! header-row (create-cell-style! wb {:background :yellow,
                                                     :font {:bold true}}))
  (save-workbook! "scraped-data.xlsx" wb))

