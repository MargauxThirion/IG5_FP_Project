import org.mongodb.scala._
import play.api.libs.json._
import scala.io.Source

object JsonToMongoDB {

  def main(args: Array[String]): Unit = {
    // Connexion à MongoDB
    val mongoClient: MongoClient = MongoClient("mongodb+srv://<username>:<password>@<cluster-url>/test?retryWrites=true&w=majority")
    val database: MongoDatabase = mongoClient.getDatabase("yourDatabase")
    val collection: MongoCollection[Document] = database.getCollection("yourCollection")

    // Chargement des données JSON
    val json: String = Source.fromURL("https://path-to-nist-json-data.json").mkString
    val jsonData: JsValue = Json.parse(json)

    // Traitement des données JSON
    jsonData.validate[Array[YourDataModel]] match {
      case JsSuccess(dataArray, _) =>
        val documents = dataArray.map(data => Document(data.toJson.toString()))
        collection.insertMany(documents).results() // Insérer dans MongoDB
      case JsError(errors) =>
        println("Erreur lors du parsing des données JSON : " + errors)
    }

    // Fermer la connexion
    mongoClient.close()
  }

  // Assurer la conversion en modèle Scala (modifie selon ta structure de données)
  case class YourDataModel(field1: String, field2: Int, etc: Boolean)
  implicit val yourDataModelFormat: Format[YourDataModel] = Json.format[YourDataModel]
}
