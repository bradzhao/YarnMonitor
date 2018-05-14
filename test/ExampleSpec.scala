
import collection.mutable.Stack
import org.scalatest._
/**
  *
  *
  * Project: YarnMonitor
  *
  * @author Brad Zhao
  * @version 1.0
  * @since : 2016-02-17 16:57
  * @note
  *
  *
  */
class ExampleSpec extends FlatSpec with Matchers {

  "A Stack" should "pop values in last-in-first-out order" in {
    val stack = new Stack[Int]
    stack.push(1)
    stack.push(2)
    stack.pop() should be (2)
    stack.pop() should be (1)
  }

  it should "throw NoSuchElementException if any empty stack is popped" in {
    val emptyStack = new Stack[Int]
    a [NoSuchElementException] should be thrownBy(emptyStack.pop())
  }
}
