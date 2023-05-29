package plugin;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.POP;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * plugin
 */
public class TestMethodVisitor extends MethodVisitor {

    public TestMethodVisitor(MethodVisitor methodVisitor) {
        super(ASM9, methodVisitor);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {

        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);

        if (owner.contains("/Thread") && name.contains("init")) {
            //方法执行之前打印
/*            mv.visitLdcInsn(" before method exec");
//            mv.visitLdcInsn(" [ASM 测试] method in " + owner + " ,name=" + name);
            mv.visitMethodInsn(INVOKESTATIC,
                    "android/util/Log", "i", "(Ljava/lang/String;Ljava/lang/String;)I", false);
            mv.visitInsn(POP);*/

/*            visitMethodInsn(INVOKESTATIC, "java/util/concurrent/Executors", "newFixedThreadPool", "(I)Ljava/util/concurrent/ExecutorService;", false);
            visitInsn(POP);

            visitMethodInsn(INVOKEINTERFACE, "java/util/concurrent/ExecutorService", "submit", "(Ljava/lang/Runnable;)Ljava/util/concurrent/Future;", true);
            visitInsn(POP);*/

        }else {

        }


    }

    @Override
    public void visitCode() {
        super.visitCode();
    }

    @Override
    public void visitVarInsn(int opcode, int varIndex) {
        super.visitVarInsn(opcode, varIndex);
    }

    @Override
    public void visitLabel(Label label) {
        super.visitLabel(label);
    }

    @Override
    public void visitInsn(int opcode) {
        super.visitInsn(opcode);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
/*        if(!type.contains("java/lang/Thread")){

        }*/
        super.visitTypeInsn(opcode, type);
    }


    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        super.visitLocalVariable(name, descriptor, signature, start, end, index);
    }

}
