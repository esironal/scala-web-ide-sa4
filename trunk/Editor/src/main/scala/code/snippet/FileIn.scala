package code.snippet
import net.liftweb.http._
import net.liftweb.common._
import code.comet.FileManager
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js._
import net.liftweb.http.js.JE._
import code.model.User
import code.model.Project
import scala.xml.Text

object FileIn {

    def cometFileList = {
        val projectId = S.param("id").open_!.toString
        <lift:comet type="FileList" name={ projectId }>
            <div id="projectName">Project Name</div>
            <div id="dirName">Current Directory</div>
            <ul>
                <li><a href="#">file name</a>-<a href="#">delete</a></li>
            </ul>
        </lift:comet>
    }

    def chdir = SHtml.onSubmit(s => {
        val projectId = S.param("id").open_!.toString
        val id = S.param("uid").open_!.toString
        val projectPath = Project.getProjectByIdAndByCurrentUser(projectId).path
        FileManager ! ('chdir, id, projectPath, s)
    })

    def newFile = SHtml.onSubmit(s => {
        val projectId = S.param("id").open_!.toString
        val projectPath = Project.getProjectByIdAndByCurrentUser(projectId).path
        FileManager ! ('new, projectPath, s)
        SetValById("fileIn", "")
    })

    def saveFile = SHtml.onSubmit(s => {
        val projectId = S.param("projectId").open_!.toString
        val projectPath = Project.getProjectByIdAndByCurrentUser(projectId).path
        val filePathInProject = S.param("fileId").open_!.toString
        FileManager.saveFile(projectPath + filePathInProject, s)
        Run("")
    })

    def listFiles(projectId: String, dir: String, formId: String) = {
        val projectPath = Project.getProjectByIdAndByCurrentUser(projectId).path

        def openFile(filePath: String) = {
            val filePathInProject = filePath.substring(projectPath.length)
            if ((new java.io.File(filePathInProject).isDirectory))
            SHtml.a(() => {
                FileManager ! ('chdir, formId, filePath)
                SetHtml("dirName", <span>{ filePathInProject }</span>)
            }, <span>{ filePathInProject }</span>)
            else
            SHtml.a(() => {
                SetValById("fileContent", FileManager.openFile(filePath)) &
                SetValById("projectId", projectId) &
                SetValById("fileId", filePathInProject) &
                SetHtml("fileName", <span>{ filePathInProject }</span>)
            }, <span>{ filePathInProject }</span>)
        }

        def deleteFile(filePath: String) = {
            SHtml.a(() => {
                FileManager ! ('delete -> filePath)
                Run("")
            }, <span>delete</span>)
        }

        for (filePath <- FileManager.fileList(projectPath + "/" + dir)) yield {
            openFile(filePath) :+ Text(" - ") :+ deleteFile(filePath)
        }
    }

    def projectList = {
        User.currentUser match {
            case Full(user) =>
                <ul>{
                    user.projects.map(project =>
                        <li>{ SHtml.link("/project?id=" + project.id.toString, () => (), Text(project.name)) }</li>)
                }</ul>
            case _ => Text("not logged in")
        }
    }

    def hiddenProjectId = {
        val projectId = S.param("id").open_!.toString
        <input type="hidden" id="id" name="id" value={ projectId }/>
    }
}
