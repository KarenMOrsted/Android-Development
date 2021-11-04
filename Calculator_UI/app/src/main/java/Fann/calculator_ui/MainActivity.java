package Fann.calculator_ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Stack;
import java.util.Vector;

public class MainActivity extends Activity {
    String input="";
   // int dotCount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // dotCount=0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button one=findViewById(R.id.oneButton);
        Button two=findViewById(R.id.twoButton);
        Button three=findViewById(R.id.threeButton);
        Button four=findViewById(R.id.fourButton);
        Button five=findViewById(R.id.fiveButton);
        Button six=findViewById(R.id.sixButton);
        Button seven=findViewById(R.id.sevenButton);
        Button eight=findViewById(R.id.eightButton);
        Button nine=findViewById(R.id.nineButton);
        Button zero=findViewById(R.id.zeroButton);
        Button dot=findViewById(R.id.dotButton);
        Button add=findViewById(R.id.addButton);
        Button sub=findViewById(R.id.subButton);
        Button multi=findViewById(R.id.multiButton);
        Button divide=findViewById(R.id.divideButton);
        Button mod=findViewById(R.id.modButton);
        Button cal=findViewById(R.id.calButton);
        Button clear=findViewById(R.id.clearButton);
        Button del=findViewById(R.id.delButton);
        Button root=findViewById(R.id.rootButton);
        Button opp=findViewById(R.id.oppButton);

        TextView inputText=findViewById(R.id.inputText);
        TextView resultText=findViewById(R.id.resultText);
        inputText.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
        resultText.setTextSize(TypedValue.COMPLEX_UNIT_SP,50);
        one.setOnClickListener(v -> {
            NumStd();
            input+=one.getText();
            inputText.setText(input);

        });
        two.setOnClickListener(v -> {
            NumStd();
            input+=two.getText();
            inputText.setText(input);

        });
        three.setOnClickListener(v -> {
            NumStd();
            input+=three.getText();
            inputText.setText(input);

        });
        four.setOnClickListener(v -> {
            NumStd();
            input+=four.getText();
            inputText.setText(input);
        });
        five.setOnClickListener(v -> {
            NumStd();
            input+=five.getText();
            inputText.setText(input);
        });
        six.setOnClickListener(v -> {
            NumStd();
            input+=six.getText();
            inputText.setText(input);
        });
        seven.setOnClickListener(v -> {
            NumStd();
            input+=seven.getText();
            inputText.setText(input);
        });
        eight.setOnClickListener(v -> {
            NumStd();
            input+=eight.getText();
            inputText.setText(input);
        });
        nine.setOnClickListener(v -> {
            NumStd();
            input+=nine.getText();
            inputText.setText(input);
        });
        zero.setOnClickListener(v->{
            if(input.length()>=1&&input.charAt(input.length()-1)=='/')
                Toast.makeText(MainActivity.this,"0不能做除数",Toast.LENGTH_LONG).show();
            NumStd();
            input+=zero.getText();
            inputText.setText(input);

        });
        //清除输入框和计算式
        clear.setOnClickListener(v->{
            input="";
            inputText.setText(input);
            resultText.setText("0");
        });
        //删除一个输入的字符
        del.setOnClickListener(v -> {
            del();
            inputText.setText(input);
        });

        add.setOnClickListener(v->{
            //规范输入符
            OpStd();
            input+=add.getText();
            inputText.setText(input);
        });

        sub.setOnClickListener(v->{
            OpStd();
            input+=sub.getText();
            inputText.setText(input);
        });

        divide.setOnClickListener(v->{
            OpStd();
            input+=divide.getText();
            inputText.setText(input);
        });

        multi.setOnClickListener(v->{
            OpStd();
            input+=multi.getText();
            inputText.setText(input);
        });

        mod.setOnClickListener(v->{
            OpStd();
            input+=mod.getText();
            inputText.setText(input);
        });
        dot.setOnClickListener(v->{
            OpStd();
            input+=dot.getText();

            // 当前一个操作数为负数时不允许输入小数点
            if(input.length()>=2&&input.charAt(input.length()-2)==')')
                input=input.substring(0,input.length()-1);
            inputText.setText(input);

        });

        root.setOnClickListener(v -> {
           /* OpStd();
            inputText.setText(input);
            if(input.isEmpty())
                return;
            double result=Double.parseDouble(calculate());
            if(result<0)
                Toast.makeText(MainActivity.this,"负数不能开平方根",Toast.LENGTH_LONG).show();
            else {
                result = Math.sqrt(result);
                input=String.valueOf(result);
                resultText.setText(input);
            }
*/
            if(input.isEmpty())
                return;
            OpStd();
            inputText.setText(input);
            String result=calculate();
            if(result.charAt(0)=='(')
                result=result.substring(1,result.length()-1);
            if(Double.parseDouble(result)<0) {
                Toast.makeText(MainActivity.this, "负数不能开平方根", Toast.LENGTH_SHORT).show();
                result="错误";
            }
            else
            {
                double resultDouble=Double.parseDouble(result);
                result=String.valueOf(Math.sqrt(resultDouble));
            }
            if(result!="错误")
                input=result;
            resultText.setText(result);
        });

        opp.setOnClickListener(v -> {
            //当输入框的最后一个字符为操作符时不进行操作 √
            //将最后一个字转化为负数 操作方法为输入了数字后再按下opp才会将其取反 √
            //输入框内给数字加上括号 如2+1 opp-> 2+(-1)  1 opp-> (-1)  (-12) opp -> 12 √
            //需要考虑的对应操作有 1.运算时将整个带括号的数字转化为正确数字 √
            // 2.delete时如何delete？将整个负数带括号一起删除 √
            //负数后再输入一个数字、小数点应该怎样处理：数字替代当前负数  小数点不允许打出 √
            //得出负数结果后 想要对负数结果进行操作 如何给该负数加上括号再填入输入框
            //一个小问题 带负数的运算式 运算完毕后输入框的最后一个括号会消失

            OpStd();
            if(input.isEmpty())
                return;
            opp();
            inputText.setText(input);


        });

        cal.setOnClickListener(v->{

            //当没有输入时，不进行运算操作
            if(input.isEmpty())
                return ;
            //当最后输入的一个字符为运算符时，删去该运算符
            if(isOperator(input.charAt(input.length()-1))) {
                input = input.substring(0, input.length() - 1);
                inputText.setText(input);
            }


            String result=calculate();
            if(result!="错误")
                input=result;
            resultText.setText(result);


        });


    }

    //删除函数
    private void del()
    {
        //当输入框为空则不作操作
        if(input.isEmpty())
            return;
        //当要删除的是一个负数

        if(input.charAt(input.length()-1)==')')
        {
            char c;
            for(int i=input.length()-1;i>=0;i--)
            {
                c=input.charAt(i);
                //将当前的所遍历的字符删除
                input = input.substring(0, i);
                //当遍历到左括号时结束
                if(c=='(')
                    break;
            }
        }
        //不是负数 是整数或者其他字符时正常删除
        else
            input=input.substring(0,input.length()-1);
    }

    //取反函数
    private void opp()
    {
        //当输入框的最后一个字符为操作符时不进行操作
        if(isOperator(input.charAt(input.length()-1))&&input.charAt(input.length()-1)!=')')
            return;

        String Num="";
        char c;
     //   Toast.makeText(MainActivity.this,""+input.charAt(input.length()-1),Toast.LENGTH_SHORT).show();
        //当取反一个负数时
        if(input.charAt(input.length()-1)==')')
        {
         //   Toast.makeText(MainActivity.this,"取反负数",Toast.LENGTH_SHORT).show();
            for(int i=input.length()-1;i>=0;i--)
            {
                c=input.charAt(i);


                //将数字、小数点保留 其他字符一律忽略 直接去掉了负号、和括号
                if(!isOperator(c)||c=='.')
                    Num+=c;
                //如果是 括号负号 则删除
                else
                {
                    //将当前的所遍历的字符删除

                    input = input.substring(0, i);
                    //当遍历到左括号时结束
                    if(c=='(')
                        break;
                }
            }
            Num=reverse(Num);

        }
        //当取反一个正数时
        else
        {

            for(int i=input.length()-1;i>=0;i--)
            {
                c=input.charAt(i);

                //如果是除小数点以外的操作符 则停止遍历
                if(isOperator(c)&&c!='.')
                    break;
                //否则 将当前数字删除 并加到Num后
                Num+=c;
                input = input.substring(0, i);
            }

            Num="(-"+reverse(Num)+")";
           // Toast.makeText(MainActivity.this,"取反正数:"+Num,Toast.LENGTH_SHORT).show();

        }

        input+=Num;
      //  Toast.makeText(MainActivity.this,Num,Toast.LENGTH_SHORT).show();

    }
    /*private boolean checkDot()
    {
        String tmpNum="";
        char c;
        int dotCount=0;
        int numCount=0;
        for(int i=0;i<input.length();i++)
        {
            c=input.charAt(i);
            if(!isOperator(c))
                tmpNum+=input.charAt(i);
            if(c=='.')
                tmpNum+=input.charAt(i);
            else if((i==input.length()-1&&numCount==1)||isOperator(c))
            {
                for(int j=0;j<tmpNum.length();j++)
                {
                    if(tmpNum.charAt(j)=='.')
                        dotCount++;
                    if(dotCount>1)
                        return false;
                }

                dotCount=0;
                tmpNum="";
                numCount++;
            }
        }
        return true;

    }*/
    private String reverse(String s)
    {
        String rs="";
        for(int i=s.length()-1;i>=0;i--)
            rs+=s.charAt(i);
        return rs;
    }

    private boolean isOperator(char c)
    {
        return c < '0' || c > '9';
    }

    //操作符规范化
    private void OpStd()
    {

        //如果当前没有输入则不需规范化
        if(input.length()==0)
            return;

        char last=input.charAt(input.length()-1);

        //如果前一个字符也为操作符，则删去前一个操作符
        if(isOperator(last)&&last!=')')
            input=input.substring(0,input.length()-1);
    }
    //操作数规范化
    private void NumStd()
    {
        //如果当前没有输入则不需规范化
        if(input.length()==0)
            return;

        //当前一个操作数为负数时，将前一个负数替换为要输入的数字
        if(input.charAt(input.length()-1)==')')
        {
            int i;
            for( i=input.length()-1;i>=0;i--)
            {
                if(input.charAt(i)=='(')
                    break;
            }
            input=input.substring(0,i);
            return;
        }

            //当前一个字符为小数点时，验证当前数字小数点是否规范
        if(input.charAt(input.length()-1)=='.') {

            int dotCount=0;
            for(int i=input.length()-1;i>=0;i--)
            {
                char c=input.charAt(i);
                if(isOperator(c)&&c!='.')
                    break;
                if(c=='.')
                    dotCount++;
                if(dotCount>1)
                    break;
            }
            if(dotCount>1)
                input=input.substring(0,input.length()-1);//如果当前数字含有超过一个的小数点，则删除前一个小数点字符
        }
        //规范第一个字符为操作符的情况
        if(input.length()==1) {
            char last = input.charAt(input.length() - 1);
            //如果前一个字符为操作符，则删去前一个操作符

            if (isOperator(last)/*||last=='0'*/)
                input = input.substring(0, input.length() - 1);
        }
        /*else if(dotCount>0) //当操作数为整数且最高位为0时要删除该0
        {
            char last = input.charAt(input.length() - 1);
            char last2nd=input.charAt(input.length()-2);
            if(last=='0'&&isOperator(last2nd))
                input = input.substring(0, input.length() - 1);
        }*/

    }
    private String calculate()
    {

        //当输入为空时，不作运算
        if(input.isEmpty())
            return "0";
        Stack<Character> OP= new Stack<>();//运算符栈
        Stack<String> NUM=new Stack<>();//数字栈

        //先通过OP将input转化为后缀表达式
        char c;
        Vector<String> postFix=new Vector<>();//后缀表达式
        StringBuilder tmpNum= new StringBuilder(new String());//用于暂时存储数字

        for(int i=0;i<input.length();i++) {
            c = input.charAt(i);

            //将负数加入tmpNum 不将这个放到isOperator判断中去 是因为带括号的负数可能会在最后一位出现

            if(c=='(')
            {
                //将括号内的所有字符都加入tmpNum
                i++;
                while(true)
                {
                    if(i==input.length())
                        break;
                    c=input.charAt(i);
                    if(c==')')
                        break;
                    tmpNum.append(c);
                    i++;
                }

                //再将该负数字符串加入后缀表达式
                postFix.add(tmpNum.toString());
                tmpNum = new StringBuilder();
               // i--;//结束循环前将i减一 进入下一轮i将正好跳到负数之后的字符
                continue;
            }




            //将表达式最后一个数字加入tmpNum 并将tmpNum插入后缀表达式
            if(i==input.length()-1)
            {
                tmpNum.append(c);
                postFix.add(tmpNum.toString());
                tmpNum = new StringBuilder();
                break;
            }

            //如果是数字则将数字加入tmpNum以保证加入后缀表达式的是完整正确的数字
            if(!isOperator(c))
                tmpNum.append(c);

            //若为操作符

            if(isOperator(c))
            {
                //当为小数点时 将小数点加入tmpNum 并跳过其他操作
                if(c=='.')
                {
                    tmpNum.append(c);
                    continue;
                }
                //将储存好的数字字符串加入postFix 并清空tmpNum
                if(tmpNum.length() > 0)
                {
                    postFix.add(tmpNum.toString());
                    tmpNum = new StringBuilder();
                }
                //当OP为空或栈顶操作符优先级低于c，将操作符压入OP
                if(OP.isEmpty())
                    OP.push(c);
                else if(Priority(OP.peek())<Priority(c))
                    OP.push(c);
                //当栈顶操作符优先级不低于c，弹出栈顶元素将其插入后缀表达式，再将c压入OP
                else
                {
                    postFix.add(String.valueOf(OP.pop()));
                    OP.push(c);
                }

            }


        }
        //将OP内的操作符加到后缀表达式后

        while(!OP.isEmpty())
        {
            char peek=OP.peek();
            OP.pop();

            postFix.add(String.valueOf(peek));
        }

      /*  String s="";
        for(int i=0;i<postFix.size();i++)
            s+=" "+postFix.elementAt(i);
        return s;
*/
        //计算后缀表达式
        for(int i=0;i<postFix.size();i++)
        {
            String s=postFix.elementAt(i);
            if(s.equals("+")||s.equals("-")||s.equals("*")||s.equals("/")||s.equals("%"))
            {
                double num2=Double.parseDouble(NUM.pop());
                double num1=Double.parseDouble(NUM.pop());
                if(s.equals("+"))
                    NUM.push(String.valueOf(num1+num2));
                if(s.equals("-"))
                    NUM.push(String.valueOf(num1-num2));
                if(s.equals("*"))
                    NUM.push(String.valueOf(num1*num2));
                if(s.equals("/")) {
                    if(num2==0)
                        return "错误";
                    NUM.push(String.valueOf(num1 / num2));
                }
                if(s.equals("%")){
                    if(num2==0)
                        return "错误";
                    NUM.push(String.valueOf(num1 % num2));
                }
            }
            else
                NUM.push(s);
        }
        if(Double.parseDouble(NUM.peek())>=0)
            return NUM.pop();
        else
            return "("+NUM.pop()+")";

    }
    private int Priority(char c)
    {
        if(c=='+'||c=='-')
            return 0;
        else return 1;
    }
}