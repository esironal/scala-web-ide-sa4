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
            <div id="dirName">Current Directory</div><span id="backlink">back</span>
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
        val currentDir = S.param("currentDir").open_!.toString

        val project = Project.getProjectByIdAndByCurrentUser(projectId)
        val dirPath = project.path + currentDir

        FileManager ! ('new, dirPath, s)
        SetValById("fileIn", "")
    })

    def saveFile = SHtml.onSubmit(s => {
        val projectId = S.param("projectId").open_!.toString
        val projectPath = Project.getProjectByIdAndByCurrentUser(projectId).path
        val filePathInProject = S.param("fileId").open_!.toString
        FileManager.saveFile(projectPath + filePathInProject, s)
        Run("")
    })

    private def goToFolderLink(formId: String, fullFolderPath: String, folderPathInProject: String, text: String) = {
      SHtml.a(() => {
          FileManager ! ('chdir, formId, fullFolderPath)
          // Not sending the real path to the browser!
          SetValById("currentDir", folderPathInProject)
        }, Text(text))
    }

    def backlink(project: Project, currentDir: String, formId: String) = {
        if(project.path == currentDir) {
          Text("")
        } else {
          val backFolder = new java.io.File(currentDir).getParent
          val filePathInProject = backFolder.substring(project.path.length)
          goToFolderLink(formId, backFolder, filePathInProject, "back");
        }
    }

    def listFiles(project: Project, currentDir: String, formId: String) = {

        def openFile(filePath: String) = {
            val filePathInProject = filePath.substring(project.path.length)
            if ((new java.io.File(filePath).isDirectory))
              goToFolderLink(formId, filePath, filePathInProject, filePathInProject);
            else
              SHtml.a(() => {
                SetValById("fileContent", FileManager.openFile(filePath)) &
                SetValById("projectId", project.id.is) &
                SetValById("fileId", filePathInProject) &
                SetHtml("fileName", <span>{ filePathInProject }</span>)
              }, Text(filePathInProject))
        }

        def deleteFile(filePath: String) = {
            SHtml.a(() => {
                FileManager ! ('delete -> filePath)
                Run("")
            }, Text("delete"))
        }

        for (filePath <- FileManager.fileList(currentDir)) yield {
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

    def addUserLink = {
        val projectId = S.param("id").open_!.toString
        SHtml.link("/invite?id=" + projectId, () => (), Text("Add users to collaborate"))
    }

}
