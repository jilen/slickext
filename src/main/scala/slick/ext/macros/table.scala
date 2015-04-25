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

    def hlistConcat[T: Liftable ](elems: Iterable[T]) = {
      val HNil = q"HNil": Tree
      elems.toList.reverse.foldLeft(HNil) { (list, c) =>
        q"$c :: $list"
      }
    }

    def genHListMapping(columns: Iterable[TermName], productType: Type, productCompType: Symbol) = {

      val hlist = hlistConcat(columns)
      val columnElems = (0 until columns.size).map(i => q"x($i)")
      val productHList = hlistConcat(columns.map(n =>q"x.$n"))
      val toProduct = q"{case x => $productCompType(..$columnElems)}"
      val fromProduct = q"{x: $productType => Option($productHList)}"
      q"def * = ($hlist).shaped <> ($toProduct, $fromProduct)"
    }

    def genSimpleMapping(columns: Iterable[TermName], productCompanionType: Symbol) = {
      q"def * = (..$columns) <> ((${productCompanionType}.apply _).tupled, ${productCompanionType}.unapply)"
    }

    def getTableClass(tableClassDecls: ClassDef) = {
      val q"$mods class $tpname[..$tparams] $ctorMods(...$paramss) extends { ..$earlydefns } with ..$parents { $self => ..$stats }" = tableClassDecls
      val (productType, productFields) = typeInfo(paramType)
      val productCompanionType = paramType.typeSymbol.companion

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

      val mapping = if(columnNames.size <= 22)
        genSimpleMapping(columnNames, productCompanionType)
      else
        genHListMapping(columnNames, productType, productCompanionType)

      q"""
          import scala.slick.collection.heterogenous._
           $mods class $tpname(tag: Tag) extends Table[$productType](tag, $tableName) {
           ..$stats
           ..$columns
           $mapping
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
