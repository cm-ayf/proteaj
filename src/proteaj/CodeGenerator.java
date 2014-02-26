package proteaj;

import proteaj.error.*;
import proteaj.io.*;
import proteaj.tast.*;

import java.io.*;
import javassist.*;

public class CodeGenerator {
  public CodeGenerator() {
    this.target = null;
  }

  public CodeGenerator(String target) {
    this.target = target;
  }

  public void codegen (Program program) {
    for (ClassDeclaration clazz : program.getClasses()) codegen(clazz);
    for (OperatorModuleDeclaration syntax : program.getOperatorsModules()) codegen(syntax);
  }

  private void codegen (ClassDeclaration clazz) {
    for (MethodDeclaration method : clazz.getMethods())
      codegen(method);

    for (ConstructorDeclaration constructor : clazz.getConstructors())
      codegen(constructor);

    for (FieldDeclaration field : clazz.getFields())
      codegen(field);

    for (DefaultValueDefinition d : clazz.getDefaultValues())
      codegen(d);

    for (ClassInitializerDefinition clIni : clazz.getInitializers())
      codegen(clIni);

    try {
      if (target != null) clazz.clazz.writeFile(target);
      else clazz.clazz.writeFile();
    } catch (CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    } catch (NotFoundException e) {
      ErrorList.addError(new NotFoundError(e, clazz.filePath, 0));
    } catch (IOException e) {
      ErrorList.addError(new FileIOError("can't write class file", clazz.filePath, 0));
    }
  }

  private void codegen (OperatorModuleDeclaration syntax) {
    try {
      if(target != null) new OperatorsFile(syntax.syntax).write(target);
      else new OperatorsFile(syntax.syntax).write();
    } catch (CompileError e) {
      ErrorList.addError(e);
    }
  }

  private void codegen (MethodDeclaration method) {
    try { method.method.setBody(method.body.toJavassistCode()); }
    catch (CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private void codegen (ConstructorDeclaration constructor) {
    try { constructor.constructor.setBody(constructor.body.toJavassistCode()); }
    catch (CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private void codegen (FieldDeclaration field) {
    try {
      CtClass thisClass = field.field.getDeclaringClass();
      thisClass.removeField(field.field);
      thisClass.addField(field.field, field.body.toJavassistCode());
    } catch (NotFoundException e) {
      assert false;
      throw new RuntimeException(e);
    } catch (CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private void codegen (DefaultValueDefinition defaultValue) {
    try { defaultValue.method.setBody(defaultValue.body.toJavassistCode()); }
    catch (CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private void codegen (ClassInitializerDefinition clIni) {
    try {
      clIni.clIni.insertAfter(clIni.body.toJavassistCode());
    } catch (CannotCompileException e) {
      assert false;
      throw new RuntimeException(e);
    }
  }

  private String target;
}

