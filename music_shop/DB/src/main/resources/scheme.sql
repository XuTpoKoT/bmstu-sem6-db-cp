CREATE TABLE IF NOT EXISTS public.User (
    login 	     text  PRIMARY KEY DEFAULT gen_random_uuid()
    , password   bytea NOT NULL
    , role_      text  NOT NULL
    , first_name text
    , last_name  text
    , birth_date date
    , email      text
    , constraint check_role check (role_ in ('EMPLOYEE','CUSTOMER'))
);

CREATE TABLE IF NOT EXISTS public.Card (
    user_login     text NOT NULL REFERENCES public.User(login) ON DELETE CASCADE
    , bonuses 	   int  NOT NULL DEFAULT 0 CHECK (bonuses >= 0)
);

CREATE TABLE IF NOT EXISTS public.Manufacturer (
    id           uuid PRIMARY KEY DEFAULT gen_random_uuid()
    , name_      text NOT NULL
);

CREATE TABLE IF NOT EXISTS public.Product (
    id 	              uuid PRIMARY KEY
    , name_           text NOT NULL
    , price           int  NOT NULL
    , description     text
    , color           text
    , manufacturer_id uuid NOT NULL REFERENCES public.Manufacturer(id) ON DELETE CASCADE
    , characteristics json
);

CREATE TABLE IF NOT EXISTS public.DeliveryPoint (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid()
    , address 	  text NOT NULL
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
