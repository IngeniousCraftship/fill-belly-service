package org.household.foodie.customer.orders;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Date;

//@RunWith(SpringRunner.class)
//@DataJpaTest
public class OrdersRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrdersRepository ordersRepository;

//    @Test
    public void updateOrderPickUpDateTime() throws Exception {
        entityManager.persist(Order.builder().email("email@email.com").orderNumber("A").orderDate(new Date())
                .orderTotal(Double.valueOf(1)).build
                ());
        Assert.assertEquals(1l, ordersRepository.updateOrderPickUpDateTime("A", "email@email.com", new Date()).longValue());
    }

}