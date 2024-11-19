import play.api.libs.json._
import scala.io.Source

case class CVEDataItem(
  ID: String,
  description: String,
  severity: String,
  attackVector: String,
  baseScore: Double
)

object LocalJSONQueries {

  def main(args: Array[String]): Unit = {
    // Chemin vers vos fichiers JSON
    val fichiersJson = Seq(
      "src/main/scala/nvdce-1.1-2023.json",
      "src/main/scala/nvdce-1.1-2024.json"
    )

    // Charger les données depuis les fichiers JSON
    val cveItems = fichiersJson.flatMap(loadCVEItems)

    // Vérifier si des données ont été chargées
    if (cveItems.isEmpty) {
      println("Aucune donnée chargée.")
      return
    }

    println(s"Nombre total de CVE chargées : ${cveItems.length}")

    // Requête 1 : CVEs avec un score de gravité supérieur à 8
    val highSeverity = cveItems.filter(_.baseScore > 8)
    println(s"Nombre de CVEs avec un score de gravité supérieur à 8 : ${highSeverity.length}")

    // Requête 2 : Liste des CVEs pour lesquelles l'attaque est locale (LOCAL)
    val localAttacks = cveItems.filter(_.attackVector == "LOCAL")
    println(s"Nombre de CVEs avec une attaque locale : ${localAttacks.length}")

    // Requête 3 : CVEs triées par score de gravité décroissant
    val sortedBySeverity = cveItems.sortBy(-_.baseScore)
    println("Top 5 des CVEs avec les scores les plus élevés :")
    sortedBySeverity.take(5).foreach(cve => println(s"${cve.ID} - Score : ${cve.baseScore}"))

    // Requête 4 : Nombre de CVEs par type de gravité
    val severityCounts = cveItems.groupBy(_.severity).map { case (severity, items) =>
      (severity, items.length)
    }
    println("Nombre de CVEs par type de gravité :")
    severityCounts.foreach { case (severity, count) =>
      println(s"$severity : $count")
    }

    // Requête 5 : Liste des descriptions des CVEs publiées après une date donnée
    val recentCVEs = cveItems.filter(_.description.contains("password"))
    println(s"Nombre de CVEs liées au mot 'password' dans la description : ${recentCVEs.length}")
    recentCVEs.foreach(cve => println(s"${cve.ID}: ${cve.description}"))
  }

  /**
   * Charger les éléments CVE depuis un fichier JSON.
   */
  def loadCVEItems(filePath: String): Seq[CVEDataItem] = {
    try {
      val source = Source.fromFile(filePath)
      val jsonContent = try {
        source.mkString
      } finally {
        source.close()
      }

      // Parser le JSON
      val json = Json.parse(jsonContent)
      val cveItems = (json \ "CVE_Items").as[Seq[JsValue]]

      // Transformer les données JSON en objets Scala
      cveItems.flatMap(parseCVEItem)
    } catch {
      case ex: Exception =>
        println(s"Erreur lors du chargement du fichier $filePath : ${ex.getMessage}")
        Seq.empty
    }
  }

  /**
   * Extraire les informations pertinentes d'un élément CVE JSON.
   */
  def parseCVEItem(json: JsValue): Option[CVEDataItem] = {
    try {
      val ID = (json \ "cve" \ "CVE_data_meta" \ "ID").as[String]
      val description = (json \ "cve" \ "description" \ "description_data").as[Seq[JsValue]]
        .headOption.flatMap(d => (d \ "value").asOpt[String]).getOrElse("Aucune description")
      val severity = (json \ "impact" \ "baseMetricV3" \ "cvssV3" \ "baseSeverity").asOpt[String].getOrElse("Unknown")
      val attackVector = (json \ "impact" \ "baseMetricV3" \ "cvssV3" \ "attackVector").asOpt[String].getOrElse("Unknown")
      val baseScore = (json \ "impact" \ "baseMetricV3" \ "cvssV3" \ "baseScore").asOpt[Double].getOrElse(0.0)

      Some(CVEDataItem(ID, description, severity, attackVector, baseScore))
    } catch {
      case ex: Exception =>
        println(s"Erreur lors du parsing d'un CVE : ${ex.getMessage}")
        None
    }
  }
}
