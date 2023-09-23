CREATE TABLE IF NOT EXISTS public.User (
    login 	     text  PRIMARY KEY DEFAULT gen_random_uuid()
    , password   bytea NOT NULL
    , role_      text  NOT NULL
    , first_name text
    , last_name  text
    , birth_date date
    , email      text
    , constraint check_role check (role_ in ('EMPLOYEE','CUSTOMER', 'ADMIN'))
);

CREATE TABLE IF NOT EXISTS public.Card (
    user_login     text NOT NULL REFERENCES public.User(login) ON DELETE CASCADE
    , bonuses 	   int  NOT NULL DEFAULT 0 CHECK (bonuses >= 0)
);

CREATE TABLE IF NOT EXISTS public.Manufacturer (
    id           uuid PRIMARY KEY DEFAULT gen_random_uuid()
    , name_      text UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS public.Product (
    id 	              uuid PRIMARY KEY DEFAULT gen_random_uuid()
    , name_           text NOT NULL
    , price           int  NOT NULL
    , description     text
    , color           text
    , storage_cnt     int  NOT NULL DEFAULT 0 CHECK (storage_cnt >= 0)
    , img_ref         text
    , manufacturer_id uuid NOT NULL REFERENCES public.Manufacturer(id) ON DELETE CASCADE
    , characteristics json
);

CREATE TABLE IF NOT EXISTS public.DeliveryPoint (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid()
    , address 	  text UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS public.Order_(
    id                 uuid        PRIMARY KEY DEFAULT gen_random_uuid()
    , customer_login   text        REFERENCES public.User(login) ON DELETE CASCADE
    , employee_login   text        REFERENCES public.User(login) ON DELETE CASCADE
    , date_            timestamptz NOT NULL
    , status           text        NOT NULL
    , delivery_point_id uuid       NOT NULL REFERENCES public.DeliveryPoint(id) ON DELETE CASCADE
    , initial_cost     int         NOT NULL CHECK (initial_cost > 0)
    , paid_by_bonuses  int         NOT NULL CHECK (paid_by_bonuses <= initial_cost)
    , constraint check_status check (status in ('formed','built', 'delivered', 'received'))
);

CREATE TABLE IF NOT EXISTS public.Order_Product (
    order_id           uuid NOT NULL REFERENCES public.Order_(id)  ON DELETE CASCADE
    , product_id       uuid NOT NULL REFERENCES public.Product(id) ON DELETE CASCADE
    , price      	   int  NOT NULL CHECK (price > 0)
    , cnt_products 	   int  NOT NULL CHECK (cnt_products > 0)
);

CREATE TABLE IF NOT EXISTS public.Cart (
    login              text NOT NULL REFERENCES public.User(login) ON DELETE CASCADE
    , product_id       uuid NOT NULL REFERENCES public.Product(id) ON DELETE CASCADE
    , cnt_products 	   int  NOT NULL CHECK (cnt_products > 0)
    , primary key(login, product_id)
);

CREATE ROLE unregistered;
GRANT SELECT, INSERT ON public.User TO unregistered;
GRANT SELECT ON public.Manufacturer TO unregistered;
GRANT SELECT ON public.Product TO unregistered;
GRANT SELECT ON public.DeliveryPoint TO unregistered;
GRANT INSERT ON public.Card TO unregistered;

CREATE ROLE customer;
GRANT SELECT ON public.User TO customer;
GRANT SELECT ON public.Manufacturer TO customer;
GRANT SELECT, UPDATE ON public.Product TO customer;
GRANT SELECT ON public.DeliveryPoint TO customer;
GRANT SELECT, UPDATE ON public.Card TO customer;
GRANT SELECT, INSERT ON public.Order_ TO customer;
GRANT SELECT, INSERT ON public.Order_Product TO customer;
GRANT SELECT, INSERT, update, Delete ON public.Cart TO customer;

REVOKE SELECT, INSERT ON public.Order_ FROM customer;
REVOKE SELECT, INSERT ON public.Order_Product FROM customer;

CREATE ROLE employee;
GRANT SELECT ON public.User TO employee;
GRANT SELECT ON public.Manufacturer TO employee;
GRANT SELECT, UPDATE ON public.Product TO employee;
GRANT SELECT ON public.DeliveryPoint TO employee;
GRANT SELECT, UPDATE ON public.Card TO employee;
GRANT SELECT, INSERT, UPDATE ON public.Order_ TO employee;
GRANT SELECT, INSERT ON public.Order_Product TO employee;
GRANT SELECT, INSERT, update, Delete ON public.Cart TO employee;

CREATE ROLE admin_;
grant all privileges ON all tables in schema public to admin_;

CREATE OR REPLACE FUNCTION change_storage_cnt()
    RETURNS TRIGGER AS
$$
BEGIN
    update public.product
    SET storage_cnt = storage_cnt - new.cnt_products
    where id = new.product_id;
    RETURN new;
END;
$$ language plpgsql;

CREATE TRIGGER decrease_storage_cnt_trigger
    AFTER INSERT
    ON public.order_product
    FOR EACH ROW
EXECUTE PROCEDURE change_storage_cnt();