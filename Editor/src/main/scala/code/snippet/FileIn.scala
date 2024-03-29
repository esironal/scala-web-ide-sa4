package code.snippet

import net.liftweb.http._
import net.liftweb.common._
import net.liftweb.util.Helpers
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js._
import net.liftweb.http.js.JE._

import code.model.User
import code.model.Project
import code.comet.FileManager

import scala.xml.Text
import scala.xml.NodeSeq

import java.io.File

object FileIn {

    def cometFileList(in: NodeSeq): NodeSeq = {
        val projectId = S.param("id").open_!.toString
        <lift:comet type="FileList" name={ projectId }>
          {in}
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

        if(S.param("type").open_!.toString == "folder") {
          FileManager ! ('newdir, dirPath, s)
        } else {
          FileManager ! ('newfile, dirPath, s)
        }
        SetValById("fileIn", "")
    })

    def saveFile = SHtml.onSubmit(s => {
        val projectId = S.param("projectId").open_!.toString
        val projectPath = Project.getProjectByIdAndByCurrentUser(projectId).path
        val filePathInProject = S.param("fileId").open_!.toString
        FileManager.saveFile(projectPath + filePathInProject, s)
        Run("")
    })

    private def goToFolderLink(formId: String, fullFolderPath: String, folderPathInProject: String, linkBody: NodeSeq) = {
      SHtml.a(() => {
          FileManager ! ('chdir, formId, fullFolderPath)
          // Not sending the real path to the browser!
          SetValById("currentDir", folderPathInProject)
        }, linkBody)
    }

    def backlink(project: Project, currentDir: String, formId: String) = {
        if(project.path == currentDir) {
          Text("")
        } else {
          val backFolder = new java.io.File(currentDir).getParent
          val filePathInProject = backFolder.substring(project.path.length)
          goToFolderLink(
            formId, backFolder, filePathInProject, 
            <img id="back_img" src="/filelist-template/img/back.png"/>
          )
        }
    }

    def listFiles(project: Project, currentDir: String, formId: String) = {

        def openFile(file: File) = {
            val filePath = file.getPath
            val filePathInProject = filePath.substring(project.path.length)
            if ((new java.io.File(filePath).isDirectory))
              goToFolderLink(
                formId, filePath, filePathInProject, 
                <img src="/filelist-template/img/folder.png" class="folder_img" /> 
                <span class="folder_text">{ Text(file.getName) }</span>
                <img class="right" src="/filelist-template/img/right.png"/>
              )
            else
            <a class="fileName" href={Helpers.appendParams("editor", ("id" -> project.id.is.toString) :: ("path" -> filePathInProject) :: Nil)}><img src="/filelist-template/img/file.png" class="file_img" /><span>{file.getName}</span></a>        }

        def deleteFile(file: File) = {
            val fileName = file.getName
            val folderPath = file.getParent
            SHtml.a(() => {
                FileManager ! ('delete, folderPath, fileName)
                Run("")
            }, <img class="delete" src="/filelist-template/img/delete.png"/>)
        }

        try {
          for (file <- FileManager.fileList(currentDir)) yield {
            openFile(file) :+ deleteFile(file)
          }
        } catch {
          case e: java.io.FileNotFoundException => Text("Project folder not found")
        }
    }

    def projectList = {
        User.currentUser match {
            case Full(user) =>
                <ul class="list">{
                    user.projects.map(project =>
                        <li>{ SHtml.link("/editor?id=" + project.id.toString, () => (), Text(project.name)) }</li>)
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
        <a href={"/invite?id=" + projectId}><span>Share</span></a>
    }

    def projectNameWithLink = {
      val projectId = S.param("id").open_!.toString
      val project = Project.getProjectByIdAndByCurrentUser(projectId)
      <a href={"/editor?id=" + projectId}>{project.name}</a>
    }
}
