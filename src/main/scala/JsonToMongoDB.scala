import org.mongodb.scala._
import org.mongodb.scala.result.InsertOneResult
import play.api.libs.json._
import scala.io.Source
import java.nio.file.{Paths, Files}
import java.nio.charset.StandardCharsets

object JsonToMongoDB {
  def main(args: Array[String]): Unit = {
    // Remplace avec ton URI de connexion et les détails spécifiques à ta base de données MongoDB
    val mongoClient: MongoClient = MongoClient("mongodb+srv://aurianepoirier:9EiwTtJk4Isz6IV1@tp-note.qkqa2.mongodb.net/TP-note?retryWrites=true&w=majority")
    val database: MongoDatabase = mongoClient.getDatabase("NVDCVE")
    val collection: MongoCollection[Document] = database.getCollection("cve2023")

    // Chemin vers le fichier JSON local
    val path = Paths.get("/Users/aurianepoirier/Library/CloudStorage/OneDrive-umontpellier.fr/POLYTECH/S9/Prog_Fonctionnelle/TPS/IG5_FP_Project/nvdcve-1.1-2023.json")
    val json: String = new String(Files.readAllBytes(path), StandardCharsets.UTF_8)
    val jsonData: JsValue = Json.parse(json)

    // Insertion du JSON dans MongoDB
    val document = Document(jsonData.toString())
    collection.insertOne(document).subscribe(
      new Observer[InsertOneResult] {
        override def onNext(result: InsertOneResult): Unit = println("Insertion succeeded")
        override def onError(e: Throwable): Unit = println(s"Insertion failed: ${e.getMessage}")
        override def onComplete(): Unit = println("Insertion completed")
      }
    )

    // Attendre un peu pour laisser le temps à l'opération de se terminer
    Thread.sleep(5000)  // Cela bloque le thread principal pour attendre que l'opération asynchrone se termine.

    // Fermer la connexion
    mongoClient.close()
  }
}