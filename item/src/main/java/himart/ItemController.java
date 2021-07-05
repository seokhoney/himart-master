package himart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

 @RestController
 public class ItemController {
     @Autowired
     ItemRepository itemRepository;


@RequestMapping(value = "/chkAndModifyStock",
method = RequestMethod.GET,
produces = "application/json;charset=UTF-8")

public boolean modifyStock(HttpServletRequest request, HttpServletResponse response)
    throws Exception {
            boolean status = false;
            Long itemId = Long.valueOf(request.getParameter("itemId"));
            int qty = Integer.parseInt(request.getParameter("qty"));

            Item item = itemRepository.findByItemId(itemId);

            if(item != null){
                    if (item.getStock() >= qty) {
                        item.setStock(item.getStock() - qty);
                        itemRepository.save(item);
                        status = true;
                    }
            }

            return status;
    }
}   
