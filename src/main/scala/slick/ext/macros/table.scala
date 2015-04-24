package slick.ext.macros

import scala.reflect.macros.blackbox.Context
import scala.reflect._
import scala.language.experimental.macros
import scala.annotation.StaticAnnotation

class table[T](tableName: String) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro SlickExtMacros.tableImpl
}

object SlickExtMacros {


  def tableImpl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    val typeTree = c.macroApplication
    val q"new $annotationTpe[$paramTypeTree](..$params).$method(..$methodParams)" = typeTree
    val paramTypeName = TypeName(paramTypeTree.toString)

    val paramType = c.typecheck(q"??? : $paramTypeName").tpe


    def typeInfo(tpe: Type) = {
      val fieldSymbols = tpe.decls.collect {
        case m: MethodSymbol if m.isCaseAccessor => m
      }
      tpe-> fieldSymbols
    }

    def getTableName(params: List[Tree]): String = {
      params.map(decodeAnnotateParam(_)).collectFirst {
        case (name, v) if name == "tableName" => v.toString
      }.getOrElse(paramType.toString)
    }

    def decodeAnnotateParam(param: Tree) = {
      val q"$fname = $fv" = param
      fname.toString -> fv
    }

    def getDefinedColumns(methods: Seq[Tree]) = {
      methods.collect {
        case m: DefDef =>
          val q" def $name = $expr" = m
          name.decodedName.toString
      }
    }

    def getTableClass(tableClassDecls: ClassDef) = {
      val q"$mods class $tpname[..$tparams] $ctorMods(...$paramss) extends { ..$earlydefns } with ..$parents { $self => ..$stats }" = tableClassDecls
      val (productType, productFields) = typeInfo(paramType)
      val productCompanionType = paramType.typeSymbol.companion

      if(productFields.size > 22) {
        c.abort(c.enclosingPosition, "Only support 1-22 case class")
      }

      val tableName = getTableName(params)

      val defined = getDefinedColumns(stats)

      val notDefinedFields = productFields.filterNot {
        case f => defined.contains(f.name.decodedName.toString)
      }

      val columns = notDefinedFields.map { f =>
        def snakify(name: String) = "[A-Z\\d]".r.replaceAllIn(name, {m =>
          "_" + m.group(0).toLowerCase()
        })
        val fName = f.name
        val fType = f.returnType
        val columnName = Literal(Constant(snakify(fName.decodedName.toString)))
        if(fName.decodedName.toString == "id" && (fType =:= typeOf[Option[Long]] || fType =:= typeOf[Option[Int]] )) {
          q"""
         def ${fName.toTermName} = column[$fType]($columnName, O.PrimaryKey, O.AutoInc)
        """
        } else {
          q"""
         def ${fName.toTermName} = column[$fType]($columnName)
        """
        }
      }

      val columnNames = productFields.map(_.name)

      q"""
           $mods class $tpname(tag: Tag) extends Table[$productType](tag, $tableName) {
           ..$stats
           ..$columns
           def * = (..$columnNames) <> ((${productCompanionType}.apply _).tupled, ${productCompanionType}.unapply)
          }

          val ${tpname.toTermName} = TableQuery[${tpname}]
      """
    }

    def genCode(classDef: ClassDef, compDef: Option[ModuleDef]) = {
      val code = getTableClass(classDef)
      code
    }

    annottees.map(_.tree) match {
      case (classDecl: ClassDef) :: Nil =>
        c.Expr(genCode(classDecl, None))
      case _ => c.abort(c.enclosingPosition, "Invalid annottee")
    }
  }
}
